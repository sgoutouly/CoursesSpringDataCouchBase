package fr.laposte.disf.fwmc.arch.isolation.nosql;

import static java.lang.System.out;

import java.io.PrintStream;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.SerializableDocument;
import com.couchbase.client.java.view.AsyncViewResult;
import com.couchbase.client.java.view.AsyncViewRow;


/**
 * @author sylvain
 *
 */
public class Functions {
	
	@NoArgsConstructor
	public static class EmptySerializableOnError implements Func1<Throwable, Observable<SerializableDocument>> {
		@Override
		public Observable<SerializableDocument> call(Throwable error) {
			return Observable.empty();
		}
		public static EmptySerializableOnError of() {
			return new EmptySerializableOnError();
		}
	}
	
	@RequiredArgsConstructor	
	public static class DumpError implements Action1<Throwable> {
		private final PrintStream out;
		@Override
		public void call(Throwable error) {
			out.println("Message Erreur : " + error.getMessage());
			out.println("Type Erreur : " + error.getClass().getName());
			error.printStackTrace();
		}
		public static DumpError of(PrintStream out) {
			return new DumpError(out);
		}		
	}
	
	@RequiredArgsConstructor
	public static final class DumpString implements Action1<String> {
		private final String message;
		@Override
		public void call(String id) {
			out.println(message + " : [" + id + "]");
		}
		public static DumpString of(String message) {
			return new DumpString(message);
		}
	}
	
	@RequiredArgsConstructor
	public static class Remove implements Func1<String, Observable<SerializableDocument>> {
		private final AsyncBucket bucket;
		@Override
		public Observable<SerializableDocument> call(String id) {
			return bucket.remove(id, SerializableDocument.class);
		}
		public static Remove of(AsyncBucket bucket) {
			return new Remove(bucket);
		}
	}
	
	@NoArgsConstructor
	public static class Rows implements Func1<AsyncViewResult, Observable<AsyncViewRow>> {
		@Override
		public Observable<AsyncViewRow> call(AsyncViewResult result) {
			return result.rows();
		}
		public static Rows of() {
			return new Rows();
		}
	}
	
	@NoArgsConstructor
	public static class Id implements Func1<AsyncViewRow, String> {
		@Override
		public String call(AsyncViewRow row) {
			return row.id();
		}
		public static Id of() {
			return new Id();
		}
	}
	
	
	/**
	 * 
	 */
	private Functions() {
		// TODO Auto-generated constructor stub
	}

}
