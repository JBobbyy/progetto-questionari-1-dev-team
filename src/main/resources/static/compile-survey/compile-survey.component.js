'use strict';

app.component("compileSurvey", {
	templateUrl: "compile-survey/compile-survey.template.html",
	controller: function($scope, $http, $routeParams, $uibModal, AnswerFactory) {

		$scope.model = {};
		$scope.options = [];
		$scope.survey = {};
		$scope.questions = [];
		$scope.answers = [];
		$scope.currentAnswer = {};
		$scope.questionIndex = 0;
		$scope.surveyMode = "INSERT";
		$scope.questionMode = "INSERT";

		$scope.load = function() {

			$http.get("/api/findSurvey?id=" + $routeParams.surveyId).then(function onFulfilled(response) {

				$scope.survey = response.data;
			}, function errorCallback(response) {

				if (response.status === 404) {
					alert("Survey not found.");
				} else {
					alert("Error");
					console.error(response);
				}
			});

			$http.get("/api/findQuestionForSurvey/" + $routeParams.surveyId).then(function onFulfilled(response) {

				$scope.questions = response.data;

				$http.get("/api/findSurveyAnswersForUser/?surveyId=" + $routeParams.surveyId + "&userId=1")
					.then(function onFulfilled(response) {

						$scope.mode = "EDIT";
						$scope.surveyMode = "EDIT";
						$scope.answers = response.data;
						$scope.selectQuestion();
					}, function errorCallback(response) {

						if (response.status === 404) {
							$scope.mode = "INSERT";
							$scope.surveyMode = "INSERT";
						} else {
							alert("Error");
							console.error(response);
						}
					});
			}, function errorCallback(response) {

				if (response.status === 404) {
					alert("No questions found.");
				} else {
					alert("Error");
					console.error(response);
				}
			});
		}

		$scope.selectQuestion = function() {

			if ($scope.answers.length < 0)
				return;

			$scope.currentAnswer = {};
			if ($scope.questionIndex < $scope.questions.length) {
				for (let answer of $scope.answers) {
					if (answer.questionDTO.id === $scope.questions[$scope.questionIndex].id) {
						$scope.currentAnswer = answer;
						if ($scope.questions[$scope.questionIndex].questionType === "SINGLECLOSED")
							$scope.model.closeended_answer = answer.closeEndedAnswerDTOs[0].id;
						else if ($scope.questions[$scope.questionIndex].questionType === "MULTIPLECLOSED") {
							for (let closeEndedAnswer in $scope.questions[$scope.questionIndex].closeEndedAnswerDTOSet)
								if (answer.closeEndedAnswerDTOs
									.find(v => v.id === $scope.questions[$scope.questionIndex].closeEndedAnswerDTOSet[closeEndedAnswer].id))
									$scope.options[closeEndedAnswer] = true;
						}
						$scope.mode = "EDIT";
						break;
					}
				}
			}
			if (!$scope.currentAnswer.id)
				$scope.mode = "INSERT";
		}

		$scope.skipQuestion = function() {

			if ($scope.questionIndex !== $scope.questions.length - 1) {
				$scope.questionIndex += 1;
				$scope.selectQuestion();
			} else {
				$scope.closeSurvey();
			}
		}

		$scope.closeSurvey = function() {

			if ($scope.surveyMode === "INSERT")
				$scope.saveAnswers();
			else
				$scope.saveModifiedAnswers();
		}

		$scope.getInputValues = function(data) {

			switch ($scope.questions[$scope.questionIndex].questionType) {
				case "OPEN":
					data.answerText = $scope.openended_answer
					break;
				case "SINGLECLOSED":
					data.closeEndedAnswerDTOs = [{
						id: $scope.model.closeended_answer
					}];
					break;
				case "MULTIPLECLOSED":
					data.closeEndedAnswerDTOs = [];
					for (let answer in $scope.questions[$scope.questionIndex].closeEndedAnswerDTOSet) {
						if ($scope.options[answer] && $scope.options[answer] === true)
							data.closeEndedAnswerDTOs.push({
								id: $scope.questions[$scope.questionIndex].closeEndedAnswerDTOSet[answer].id
							})
					}
			}
		}

		$scope.insert = function() {

			let data = AnswerFactory.createAnswer($scope.currentAnswer.id, 1, $routeParams.surveyId,
				$scope.questions[$scope.questionIndex].id);

			$scope.getInputValues(data);

			$http.post("/api/addAnswer", data).then(function onFulfilled() {

				$scope.skipQuestion();
			}, function errorCallback(response) {

				alert("Error");
				console.error(response);
			});
		}

		$scope.modify = function() {

			let data = AnswerFactory.createAnswer($scope.currentAnswer.id, 1, $routeParams.surveyId,
				$scope.questions[$scope.questionIndex].id);

			$scope.getInputValues(data);

			$http.patch("/api/modifyAnswer", data).then(function onFulfilled() {

				$scope.skipQuestion();
			}, function errorCallback(response) {

				alert("Error");
				console.error(response);
			});
		}

		$scope.delete = function() {

			$http.delete("/api/deleteAnswer/" + $scope.currentAnswer.id).then(function onFulfilled() {

				$scope.skipQuestion();
			}, function errorCallback(response) {

				alert("Error");
				console.error(response);
			});
		}

		$scope.saveAnswers = function() {

			let modal = $uibModal.open({
				animation: true,
				windowClass: "show",
				templateUrl: "template/close-survey.template.html",
				controller: function($scope, $http, $location) {

					$scope.submit = function() {

						$http.post("/api/saveSurveyAnswers?surveyId=" + $routeParams.surveyId + "&userId=1")
							.then(function onFulfilled() {

								modal.close();
								$location.path("/");
							}, function errorCallback(response) {

								modal.close();
								alert("Error");
								console.error(response);
							});
					}

					$scope.cancel = function() {

						modal.close();
					}
				}
			});
		}

		$scope.saveModifiedAnswers = function() {

			let modal = $uibModal.open({
				animation: true,
				windowClass: "show",
				templateUrl: "template/close-survey.template.html",
				controller: function($scope, $http, $location) {

					$scope.submit = function() {

						$http.post("/api/saveModifiedSurveyAnswers?surveyId=" + $routeParams.surveyId + "&userId=1")
							.then(function onFulfilled() {

								modal.close();
								$location.path("/");
							}, function errorCallback(response) {

								modal.close();
								alert("Error");
								console.error(response);
							});
					}

					$scope.cancel = function() {

						modal.close();
					}
				}
			});
		}
	}
});