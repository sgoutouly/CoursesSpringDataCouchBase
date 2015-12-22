var coursesApp=angular.module("courses",["ngRoute","ngAnimate","ToolBox.directives","ToolBox.services","courses.errors","courses.services","courses.controleurs","chat.services","chat.controleurs"]),modChatServices=angular.module("chat.services",[]);modChatServices.factory("ComposantChat",["$q","$rootScope",function(a,b){var c=window.MozWebSocket?MozWebSocket:WebSocket,d=null,e={};return e.connect=function(){d=new c("ws://"+location.host+jsRoutes.controllers.Chat.connect().url)},e.onmessage=function(a){d.onmessage=function(c){b.$apply(function(){a.apply(d,[JSON.parse(c.data)])})}},e.disconnect=function(){d.close()},e.send=function(a){d.send(JSON.stringify(a))},e}]);var modChatControleurs=angular.module("chat.controleurs",[]);modChatControleurs.controller("OpenChatCtrl",["$scope","ComposantChat",function(a,b){a.message,a.messages=[],a.members=[],b.connect(),b.onmessage(function(b){a.messages.push(b),a.members=b.members}),a.sendMessage=function(c){c&&13!==c.keyCode||(c&&c.preventDefault(),b.send({text:a.message}),a.message="")},a.disconnect=function(){b.disconnect()}}]),coursesApp.config(["$routeProvider",function(a){a.when("/openChat",{templateUrl:"partials/chat.html",controller:"OpenChatCtrl"})}]);var modControleurs=angular.module("courses.controleurs",[]);coursesApp.config(["$routeProvider",function(a){a.when("/creationListe",{templateUrl:"partials/creationListe.html",controller:"CreationListeCtrl"}).when("/modificationListe/:listeId",{templateUrl:"partials/creationListe.html",controller:"ModificationListeCtrl"}).when("/listesEnCours",{templateUrl:"partials/listesEnCours.html",controller:"AffichageListesEnCoursCtrl"}).when("/listes/:listeId",{templateUrl:"partials/liste-detail.html",controller:"ListeDetailCtrl"})}]),modControleurs.controller("CreationListeCtrl",["$scope","ComposantParametres","ComposantListe","ajouterCourse",function(a,b,c,d){a.dateRedaction=(new Date).toLocaleDateString(),a.formDesignation,a.formQte,a.formUnite,a.formCourses=[],a.messageWait="Chargement des données ...",b.lire().then(function(b){a.paramListeProduits=[{designation:"ajouter un nouveau produit"}].concat(b.produits),a.paramListeUnites=[{designation:"ajouter une nouvelle unité"}].concat(b.unites),a.messageWait=""}),a.addCourse=function(){d(a.formCourses,{designation:a.formDesignation.designation,qte:a.formQte,unite:a.formUnite.designation}),a.formDesignation="",a.formQte="",a.formUnite=""},a.deleteCourse=function(b){a.formCourses.splice(b,1)},a.saveListe=function(){c.creerListe({dateRedaction:a.dateRedaction,courses:a.formCourses}).then(function(){a.formDesignation="",a.formQte="",a.formCourses=[],location.hash="#/listesEnCours"})},a.goHome=function(){location.hash="#/listesEnCours"}}]),modControleurs.controller("ModificationListeCtrl",["$scope","$routeParams","ComposantParametres","ComposantListe","ajouterCourse",function(a,b,c,d,e){a.formListeId=b.listeId,a.formDesignation,a.formQte,a.formUnite,a.messageWait="Chargement des données ...",d.consulterListe(a.formListeId).then(function(b){a.dateRedaction=b.dateRedaction,a.formCourses=b.courses}),c.lire().then(function(b){a.paramListeProduits=[{designation:"ajouter un nouveau produit"}].concat(b.produits),a.paramListeUnites=[{designation:"ajouter une nouvelle unité"}].concat(b.unites),a.messageWait=""}),a.addCourse=function(){e(a.formCourses,{designation:a.formDesignation.designation,qte:a.formQte,unite:a.formUnite.designation}),a.formDesignation="",a.formQte="",a.formUnite=""},a.deleteCourse=function(b){a.formCourses.splice(b,1)},a.saveListe=function(){d.modifierListe(a.formListeId,{id:a.formListeId,dateRedaction:a.dateRedaction,courses:a.formCourses}).then(function(){a.formDesignation="",a.formQte="",a.formCourses=[],location.hash="#/listesEnCours"})},a.goHome=function(){location.hash="#/listesEnCours"}}]),modControleurs.controller("AffichageListesEnCoursCtrl",["$scope","ComposantListe",function(a,b){a.messageWait="Chargement des données ...",b.rechercherListe().then(function(b){a.listes=b,a.messageWait=""}),a.deleteListe=function(c){confirm("Etes-vous sûr de vouloir supprimer cette liste ?")&&b.supprimerListe(a.listes[c].id).then(function(){a.listes.splice(c,1)})},a.copyListe=function(c){confirm("Etes-vous sûr de vouloir copier cette liste ?")&&b.copierListe(a.listes[c].id).then(function(b){a.listes.push(b)})},a.modifyListe=function(b){location.hash="#/modificationListe/"+a.listes[b].id},a.goHome=function(){location.hash="#/listesEnCours"}}]),modControleurs.controller("AffichageListesArchiveesCtrl",["$scope",function(a){a.listes=DB.listes}]),modControleurs.controller("MenuCtrl",["$scope","$location",function(a,b){a.navClass=function(a){var c=b.path().substring(1)||"menu";return a===c?"item-actif":""}}]);var modErrors=angular.module("courses.errors",[]);coursesApp.config(["$routeProvider",function(a){a.when("/404",{templateUrl:"partials/errors.html",controller:"ErrorCtrl"}).otherwise({redirectTo:"/404"})}]),coursesApp.run(["$rootScope","$location",function(a,b){a.$on("$routeChangeError",function(a,c,d,e){b.path("/404").replace()})}]),modErrors.controller("ErrorCtrl",["$scope",function(a){}]);var modServices=angular.module("courses.services",[]);modServices.factory("ComposantListe",["$q","toolbox_http",function(a,b){return{consulterListe:function(a){return b.get("/courses/listes/"+a)},rechercherListe:function(){return b.get("/courses/listes/")},copierListe:function(a){var c=this;return c.consulterListe(a).then(function(a){return c.creerListe({dateRedaction:(new Date).toLocaleDateString(),courses:a.courses})}).then(function(a){return b.get(a.location)})},creerListe:function(a){return b.post("/courses/listes/",a)},modifierListe:function(a,c){return b.put("/courses/listes/"+a,c)},supprimerListe:function(a){return b["delete"]("/courses/listes/"+a)}}}]),modServices.factory("ajouterCourse",function(){return function(a,b){a.push({designation:b.designation,qte:b.qte,unite:b.unite})}}),modServices.factory("ComposantParametres",["$q","toolbox_http",function(a,b){return{lire:function(){return b.get("/courses/parametres/")}}}]);var toolBoxServices=angular.module("ToolBox.services",[]);toolBoxServices.factory("toolbox_http",["$http",function(a){function b(a){alert("Erreur : "+a.status+", "+a.data)}return{get:function(c){return a.get(c).then(function(a){return a.data},function(a){b(a)})},put:function(c,d){return a.put(c,d).then(function(a){return a.data},function(a){b(a)})},post:function(c,d){return a.post(c,d).then(function(a){return a.headers()},function(a){b(a)})},"delete":function(c){return a["delete"](c).then(function(a){return a.data},function(a){b(a)})}}}]);var toolBoxDirectives=angular.module("ToolBox.directives",[]);toolBoxDirectives.directive("tbMenuLeft",function(){return{link:function(a,b,c){function d(a){b.removeClass("animate"),l=j(a).X,initialleft=b.hasClass("show")?0:-k,l<30+initialleft+k&&(b.on("touchmove",e),b.on("touchend",f))}function e(a){a.stopImmediatePropagation(),a.preventDefault(),m=j(a).X,initialleft=b.hasClass("show")?0:-k;var c=m+initialleft-l;c>0?c=0:-1*k>c&&(c=-1*k),b.prop("style").webkitTransform="translateX("+c+"px)"}function f(a){var c=m+initialleft-l;c>-1*Math.round(k/2)?(b.prop("style").webkitTransform="",b.addClass("animate"),b.addClass("show"),$shadow.addClass("show"),i()):h(a)}function g(a){a.preventDefault(),b.addClass("animate"),b.toggleClass("show"),$shadow.toggleClass("show"),i()}function h(a){b.prop("style").webkitTransform="",b.addClass("animate"),b.removeClass("show"),$shadow.removeClass("show"),i()}function i(){b.off("touchmove"),b.off("touchend")}function j(a){var b,c={},d=a||window.event;return a.touches?b=a.touches:a.originalEvent&&(b=d.originalEvent.changedTouches),b?(c.X=b[0].clientX,c.Y=b[0].clientX):(c.X=d.clientX,c.Y=d.clientX),c}var k=b.prop("offsetWidth"),l=0,m=0;bouton=document.getElementById("menuBouton"),bouton&&angular.element(bouton).on("click",g),b.on("touchstart",d),b.on("click",h),$shadow=angular.element("<div class='menu-shadow'></div>"),$shadow.on("click",h),b.parent().append($shadow)}}});