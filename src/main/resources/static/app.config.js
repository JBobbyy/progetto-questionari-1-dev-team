'use strict';

angular.
	module('UNIMIBModules').
	config(['$routeProvider',
		function config($routeProvider) {
			$routeProvider.
				when('/findSurvey', {
					template: '<find-survey></find-survey>',
				}).
				when('/modifySurvey/:idSurvey/:idUser', {
					template: '<modify-survey></modify-survey>'
				}).
				when('/editQuestion/:questionId/:userId', {
					template: '<editQuestion></editQuestion>'
				}).
				when('/addSurvey/:idUser', {
					template: '<addSurvey></addSurvey>'
				}).
				when('/compileSurvey/:idSurvey/:idUser', {
					template: '<compileSurvey></compileSurvey>'
				}).when("/", {
					templateUrl: "/template/home.html",
					controller: "homeCtrl"
				}).when("/add_answer", {
					templateUrl: "/template/add_answer.html",
					controller: "addAnswerCtrl"
				}).when("/edit_answer", {
					templateUrl: "/template/edit_answer.html",
					controller: "editAnswerCtrl"
				}).otherwise({ redirectTo: "/findSurvey" });
		}
	]);