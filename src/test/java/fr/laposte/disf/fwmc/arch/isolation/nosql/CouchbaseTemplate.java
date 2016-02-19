package fr.laposte.disf.fwmc.arch.isolation.nosql;

import static com.couchbase.client.java.view.Stale.FALSE;
import static java.lang.System.out;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.SerializableDocument;
import com.couchbase.client.java.view.AsyncViewResult;
import com.couchbase.client.java.view.AsyncViewRow;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylvaingoutouly.datacouch.config.CouchBaseConfig;

import fr.laposte.disf.fwmc.arch.isolation.nosql.Functions.DumpError;
import fr.laposte.disf.fwmc.arch.isolation.nosql.Functions.DumpString;
import fr.laposte.disf.fwmc.arch.isolation.nosql.Functions.EmptySerializableOnError;
import fr.laposte.disf.fwmc.arch.isolation.nosql.Functions.Id;
import fr.laposte.disf.fwmc.arch.isolation.nosql.Functions.Remove;
import fr.laposte.disf.fwmc.arch.isolation.nosql.Functions.Rows;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CouchBaseConfig.class })
public class CouchbaseTemplate {

	/** DEFAULT_OBJECT_MAPPER */
	private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

	/** objectMapper */
	private static volatile ObjectMapper objectMapper = null;
	
	@Autowired private CouchbaseOperations couchbaseTemplate;

	@Getter
	private static class Data implements Serializable {
		private String content = "coucou";
		private List<String> l = Arrays.asList("Un", "deux", "trois");
	}
	
	public static ObjectMapper mapper() {
		if (objectMapper == null) {
			return DEFAULT_OBJECT_MAPPER;
		}
		else {
			return objectMapper;
		}
	}
	
	/**
	 * Convert an object to JsonNode.
	 *
	 * @param data Value to convert in Json.
	 */
	public static JsonNode toJson(final Object data) {
		try {
			return mapper().valueToTree(data);
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	@Test
	public void readContent() {
		final Bucket bucket = couchbaseTemplate.getCouchbaseBucket();
		SerializableDocument d1 = SerializableDocument.create("test", new Data());
		bucket.upsert(d1);
		SerializableDocument d2 = bucket.get("test", SerializableDocument.class); 

		Serializable o = d2.content();
		System.err.println(toJson(o).toString());
		
	}
	
	@Test
	public void insertContexteProcessus() {
		final AsyncBucket bucket = couchbaseTemplate.getCouchbaseBucket().async();
		
		Observable<SerializableDocument> obs = Observable.just(
			SerializableDocument.create("ctxAssemblage_ctxProcessus#PTZR777#1", new Data()),
			SerializableDocument.create("ctxAssemblage_ctxProcessus#PTZR777#2", new Data()),
			SerializableDocument.create("ctxAssemblage_ctxProcessus#PTZR777#3", new Data()),
			SerializableDocument.create("ctxAssemblage_ctxProcessus#PTZR777#4", new Data()),
			SerializableDocument.create("ctxAssemblage_ctxProcessus#PTZR777#5", new Data())
		);
		
		obs.flatMap(new Func1<SerializableDocument, Observable<SerializableDocument>>() {
	        @Override
	        public Observable<SerializableDocument> call(final SerializableDocument docToInsert) {
	            return bucket.insert(docToInsert);
	        }
	    })
	    .last()
	    .toBlocking()
	    .single();
	}

	@Test
	public void selectOnContexteApplicatif() {
		out.println("Documents pour le contexte applicatif canal : ");
		ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler").startKey("ctxAppCanal_ctxApplication\u0023")
			.endKey("ctxAppCanal_ctxApplication\u0023\u02ad");
		ViewResult result = couchbaseTemplate.queryView(query);
		List<ViewRow> viewRows = result.allRows();
		out.println(viewRows.size());
		for (ViewRow viewRow : viewRows) {
			out.println(viewRow.id());
		}
	}
	
	@Test
	public void selectOnContexteProcessus() {
		out.println("Documents pour le contexte assemblage : ");
		ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler")
			.startKey("ctxAssemblage_ctxProcessus\u0023")
			.endKey("ctxAssemblage_ctxProcessus\u0023\u02ad");
		ViewResult result = couchbaseTemplate.queryView(query);
		List<ViewRow> viewRows = result.allRows();
		out.println(viewRows.size());
		for (ViewRow viewRow : viewRows) {
			out.println(viewRow.id());
		}
	}	

	@Test
	public void deleteContexteProcessus() {

		out.println("Traitement des Documents de cache pour le contexte processus : ");

		final AsyncBucket bucket = couchbaseTemplate.getCouchbaseBucket().async();
		final ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler")
			.startKey("ctxAssemblage_ctxProcessus")
			.endKey("ctxAssemblage_ctxProcessus\u02ad")
			.stale(FALSE);

		final Observable<Integer> obsNbDocs = bucket.query(query)
			.flatMap(Rows.of())
			.map(Id.of())
			.doOnNext(DumpString.of("Document en cours"))
			.flatMap(Remove.of(bucket))
			.doOnError(DumpError.of(out))
			.onErrorResumeNext(EmptySerializableOnError.of())
			.count();

		// On bloque pour avoir le nombre de docs à la fin, une fois que toutes les traces d'id ont été émises (async oblige)
		Integer nbDocs = obsNbDocs.toBlocking().singleOrDefault(0);
		
		out.println(nbDocs + " documents supprimés !");
	}
	
	
	@Test
	public void deleteContexteUtilisateur() {

		out.println("Traitement des Documents de cache pour le contexte utilisateur : ");

		final AsyncBucket bucket = couchbaseTemplate.getCouchbaseBucket().async();
		final ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler")
			.startKey("ctxAppCanal_ctxUtilisateur\u0023")
			.endKey("ctxAppCanal_ctxUtilisateur\u0023\u02ad")
			.stale(FALSE);

		final Observable<Integer> obsNbDocs = bucket.query(query).take(1)
			.flatMap(new Func1<AsyncViewResult, Observable<AsyncViewRow>>() {
				@Override
				public Observable<AsyncViewRow> call(AsyncViewResult result) {
					return result.rows();
				}
			}).map(new Func1<AsyncViewRow, String>() {
				@Override
				public String call(AsyncViewRow row) {
					return row.id();
				}
			}).flatMap(new Func1<String, Observable<SerializableDocument>>() {
				@Override
				public Observable<SerializableDocument> call(String id) {
					return bucket.remove(id, SerializableDocument.class);
				}
			}).doOnError(new Action1<Throwable>() {
				@Override
				public void call(Throwable error) {
					out.println("Message Erreur : " + error.getMessage());
					out.println("Type Erreur : " + error.getClass().getName());
				}
			}).onErrorResumeNext(new Func1<Throwable, Observable<SerializableDocument>>() {
				@Override
				public Observable<SerializableDocument> call(Throwable error) {
					return Observable.empty();
				}
			}).count();

		// On bloque pour avoir le nombre de docs à la fin, une fois que toutes les traces d'id ont été émises (async oblige)
		Integer nbDocs = obsNbDocs.toBlocking().singleOrDefault(0);
		
		out.println("Nb total documents : " + nbDocs + " supprimés !");
	}

	@Test
	public void deleteContexteClient() {

		out.println("Traitement des Documents de cache pour le contexte applicatif canal : ");

		final AsyncBucket bucket = couchbaseTemplate.getCouchbaseBucket().async();
		ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler").startKey("ctxAppCanal_ctxClient\u0023")
			.endKey("ctxAppCanal_ctxClient\u0023\u02ad").stale(FALSE);

		final Observable<Integer> obsNbDocs = bucket.query(query).take(1)
			.flatMap(new Func1<AsyncViewResult, Observable<AsyncViewRow>>() {

				@Override
				public Observable<AsyncViewRow> call(AsyncViewResult result) {
					return result.rows();
				}
			}).map(new Func1<AsyncViewRow, String>() {

				@Override
				public String call(AsyncViewRow row) {
					return row.id();
				}
			}).doOnNext(new Action1<String>() {

				@Override
				public void call(String id) {
					out.println("Id du document à supprimer : " + id);
				}
			}).doOnNext(new Action1<String>() {

				@Override
				public void call(final String id) {
					bucket.remove(id).subscribe(new Action1<JsonDocument>() {

						@Override
						public void call(JsonDocument doc) {
							out.println("Document : " + id + " supprimé");
						}
					});
				}
			}).doOnError(new Action1<Throwable>() {

				@Override
				public void call(Throwable error) {
					out.println("Message Erreur : " + error.getMessage());
					out.println("Type Erreur : " + error.getClass().getName());
				}
			}).onErrorResumeNext(new Func1<Throwable, Observable<String>>() {

				@Override
				public Observable<String> call(Throwable error) {
					return Observable.empty();
				}
			}).count();

		// On bloque pour avoir le nombre de docs à la fin, une fois que toutes les traces d'id ont été émises (async
		// oblige)
		Integer nbDocs = obsNbDocs.toBlocking().singleOrDefault(0);
		out.println("Nb total documents : " + nbDocs + " supprimés !");
	}

	@Test
	public void deleteContexteApplicatif() {

		out.println("Traitement des Documents de cache pour le contexte applicatif canal : ");

		final AsyncBucket bucket = couchbaseTemplate.getCouchbaseBucket().async();
		ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler").startKey("ctxAppCanal_ctxApplication\u0023")
			.endKey("ctxAppCanal_ctxApplication\u0023\u02ad").stale(FALSE);

		final Observable<Integer> obsNbDocs = bucket.query(query).take(5)
			.flatMap(new Func1<AsyncViewResult, Observable<AsyncViewRow>>() {

				@Override
				public Observable<AsyncViewRow> call(AsyncViewResult result) {
					return result.rows();
				}
			}).map(new Func1<AsyncViewRow, String>() {

				@Override
				public String call(AsyncViewRow row) {
					return row.id();
				}
			}).doOnNext(new Action1<String>() {

				@Override
				public void call(String id) {
					out.println("Id du document à supprimer : " + id);
				}
			}).doOnNext(new Action1<String>() {

				@Override
				public void call(String id) {
					bucket.remove(id, SerializableDocument.class).subscribe(new Action1<SerializableDocument>() {

						@Override
						public void call(SerializableDocument doc) {
							out.println("Document : " + doc.id() + " supprimé");
						}
					});
				}
			}).doOnError(new Action1<Throwable>() {

				@Override
				public void call(Throwable error) {
					out.println("Message Erreur : " + error.getMessage());
					out.println("Type Erreur : " + error.getClass().getName());
				}
			}).onErrorResumeNext(new Func1<Throwable, Observable<String>>() {

				@Override
				public Observable<String> call(Throwable error) {
					return Observable.empty();
				}
			}).count();

		// On bloque pour avoir le nombre de docs à la fin, une fois que toutes les traces d'id ont été émises (async
		// oblige)
		Integer nbDocs = obsNbDocs.toBlocking().singleOrDefault(0);
		out.println("Nb total documents : " + nbDocs + " supprimés !");
	}

	@Test
	public void selectOnRoutage() {
		out.println("Documents pour le routage : ");
		ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler").startKey("ctxRtgIa\u0023")
			.endKey("ctxRtgIa\u0023\u02ad");
		ViewResult result = couchbaseTemplate.queryView(query);
		List<ViewRow> viewRows = result.allRows();
		out.println(viewRows.size());
		for (ViewRow viewRow : viewRows) {
			out.println(viewRow.id());
		}
	}

	@Test
	public void deleteRoutageAsync() {

		out.println("Traitement des Documents de cache pour le routage : ");

		final AsyncBucket bucket = couchbaseTemplate.getCouchbaseBucket().async();
		final ViewQuery query = ViewQuery.from("dev_FwROA", "ContexteCrawler").startKey("ctxRtgIa\u0023")
			.endKey("ctxRtgIa\u0023\u02ad").stale(FALSE);

		final Observable<Integer> obsNbDocs = bucket.query(query)
			.flatMap(new Func1<AsyncViewResult, Observable<AsyncViewRow>>() {

				@Override
				public Observable<AsyncViewRow> call(AsyncViewResult result) {
					return result.rows();
				}
			}).map(new Func1<AsyncViewRow, String>() {

				@Override
				public String call(AsyncViewRow row) {
					return row.id();
				}
			}).doOnNext(new Action1<String>() {

				@Override
				public void call(String id) {
					out.println("Id du document : " + id);
				}
			}).doOnNext(new Action1<String>() {

				@Override
				public void call(String id) {
					bucket.remove(id, SerializableDocument.class).subscribe(new Action1<SerializableDocument>() {

						@Override
						public void call(SerializableDocument doc) {
							out.println("Document : " + doc.id() + " supprimé");
						}
					});
				}
			}).doOnError(new Action1<Throwable>() {

				@Override
				public void call(Throwable error) {
					out.println("Erreur : " + error.getMessage());
				}
			}).onErrorResumeNext(new Func1<Throwable, Observable<String>>() {

				@Override
				public Observable<String> call(Throwable error) {
					return Observable.empty();
				}
			}).count();

		// On bloque pour avoir le nombre de docs à la fin, une fois que toutes les traces d'id ont été émises (async
		// oblige)
		Integer nbDocs = obsNbDocs.toBlocking().singleOrDefault(0);
		out.println("Nb total documents : " + nbDocs + " supprimés !");
	}

}