angular.module("TeachersPetApp", ["chart.js"])
  // Optional configuration
  .config(['ChartJsProvider', function (ChartJsProvider) {
    // Configure all charts
    ChartJsProvider.setOptions({
      chartColors: ['#FF5252', '#FF8A80'],
      responsive: false
    });
    // Configure all line charts
    ChartJsProvider.setOptions('line', {
      showLines: false
    });
  }])
  .controller("GraphController", ['$scope', '$timeout', '$http', function ($scope, $timeout, $http) {

//  $scope.labels = ["January", "February", "March", "April", "May", "June", "July"];
//  $scope.series = ['Series A', 'Series B'];
//  $scope.data = [
//    [65, 59, 80, 81, 56, 55, 40],
//    [28, 48, 40, 19, 86, 27, 90]
//  ];
//  $scope.onClick = function (points, evt) {
//    console.log(points, evt);
//  };
//
//  // Simulate async data update
//  $timeout(function () {
//    $scope.data = [
//      [28, 48, 40, 19, 86, 27, 90],
//      [65, 59, 80, 81, 56, 55, 40]
//    ];
//  }, 3000);














       console.log("im before it starts")
  var getCurrentClass = function(courseId) {
            console.log("In getCurrentClass function in ng controller with courseID = " + courseId);

            $http.post("/getCurrentClass.json", courseId)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        $scope.currentClass = response.data;
//                        return response.data;


                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };



        $scope.graph = function(courseId) {
                    console.log("In graph function in ng controller with courseID = " + courseId);

                    $scope.currentClassId = courseId;

                    $http.post("/graph.json", courseId)
                        .then(
                            function successCallback(response) {
                                console.log(response.data);
                                console.log("Adding data to scope");

                                 fillGraphContainerWithResponseData(response.data);

                                 },
                                   function errorCallback(response) {
                                    console.log("Unable to get data...");
                                });

                                getCurrentClass(courseId);
                                console.log("********* Got current class ***********");
                                console.log($scope.currentClass);


                                   };

                    var fillGraphContainerWithResponseData = function(responseData) {
                             $scope.gradebookContainer = responseData;
                             // $scope.allAssignments = $scope.gradebookContainer.assignments;
                 //                        $scope.allAssignmentsToGetLength = $scope.gradebookContainer.assignments;
                             $scope.allAssignmentsToGetLength = $scope.gradebookContainer.assignmentAndAverageContainers;
                 //                        console.log("****Here is allAssignmentsToGetLength - check to make sure all in there");
                             console.log($scope.allAssignmentsToGetLength);
                             $scope.allStudentAssignments = $scope.gradebookContainer.studentContainers.studentAssignments;
                             // $scope.numberOfAssignments = $scope.allAssignments.length;
                             $scope.numberOfAssignments = $scope.allAssignmentsToGetLength.length;

                             // new all assignments by getting out of studentAssignments list
                             var allAssignments = new Array($scope.numberOfAssignments);

                             //if any of the grades are -1, we need to change them to not showing!! N/A for now.
                             for (var counter = 0; counter < $scope.gradebookContainer.studentContainers.length; counter++) {
                                 for (var insideCounter = 0; insideCounter < $scope.gradebookContainer.studentContainers[counter].studentAssignments.length; insideCounter++) {
                                     if ($scope.gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].grade === -1) {
                                         //replace -1 with blank
                 //                        console.log("!!!!!!!!!!CHANGING -1 GRADE TO BLANK!!!!!!!!!!");
                                         $scope.gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].grade = "";
                 //                        console.log($scope.gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].grade);
                                     }
                                 }
                             }

                             //loop to populate allAssignments array in the order that the grades are being displayed
                             //TOOK OUT BC IM ORDERING THEM BY DATE ON THE BACKEND!!!
                             //AND PUT THIS IN:

                             //NO I THINK KEEP THIS IN AND WILL HAVE TO DO ORDERING ONCE WE LIST STUDENT ASSIGNMENTS ON BACKEND IN POPULATING METHOD OF RETURNING STUDENT CONTAINERS IN JSON CONTROLLER
                             var currentStudentToGetAssignmentName;
                             for (var counter = 0; counter < $scope.numberOfAssignments; counter++) {
                                 if (counter == 0) {
                                     allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                 //                                console.log("****In graph loop (counter is 0)**** (counter = " + counter + ") Assignment name added: ");
                                     console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                                 } else if (!(($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name) === ($scope.gradebookContainer.studentContainers[0].studentAssignments[counter - 1].assignment.name))) {
                                     allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                 //                                console.log("****In graph loop (name isn't same as last)**** (counter = " + counter + ") Assignment name added: ");
                                     console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                                 }

                             }
                             $scope.allAssignments = allAssignments;


                             //loop to order the studentAssignments correctly (needed when new students added) AND to make sure the average doesn't show as -1 for new students
                             //TOOK THIS OUT BC WE ARE NOW ORDERING STUDENT ASSIGNMENTS BY DATE ON BACKEND
                 //            for (var counter = 0; counter < $scope.gradebookContainer.studentContainers.length; counter++) {
                 //                if ($scope.gradebookContainer.studentContainers[counter].average === -1) {
                 //                    $scope.gradebookContainer.studentContainers[counter].average = "";
                 //                }
                 //                for (var insideCounter = 0; insideCounter < $scope.gradebookContainer.studentContainers[counter].studentAssignments.length; insideCounter++) {
                 //                    if ($scope.gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].assignment.name === allAssignments[insideCounter].name) {
                 //                        //don't do anything
                 //                    } else {
                 //                        //go find this student's student assignment for assignment at index insideCounter in allAssignments and swap their positions in studentAssignments
                 //                        for (var insideInsideCounter = insideCounter; insideInsideCounter < $scope.gradebookContainer.studentContainers[counter].studentAssignments.length; insideInsideCounter++) {
                 //                            if ($scope.gradebookContainer.studentContainers[counter].studentAssignments[insideInsideCounter].assignment.name === allAssignments[insideCounter].name) {
                 //                                var temporary = $scope.gradebookContainer.studentContainers[counter].studentAssignments[insideCounter];
                 //                                $scope.gradebookContainer.studentContainers[counter].studentAssignments[insideCounter] =  $scope.gradebookContainer.studentContainers[counter].studentAssignments[insideInsideCounter];
                 //                                $scope.gradebookContainer.studentContainers[counter].studentAssignments[insideInsideCounter] = temporary;
                 //                            }
                 //                        }
                 //                    }
                 //                }

                             //Don't display average (-1) for students who have no grades entered yet
                             for (var counter = 0; counter < $scope.gradebookContainer.studentContainers.length; counter++) {
                                 if ($scope.gradebookContainer.studentContainers[counter].average === -1) {
                                     $scope.gradebookContainer.studentContainers[counter].average = "";
                                 }
                             }


                 //NEW VERSION

                             var assignmentAveragesArray = new Array($scope.numberOfAssignments);
                             //loop to populate assignmentAverages array in the order that the grades are being displayed
                             for (var counter = 0; counter < $scope.numberOfAssignments; counter++) {
                                 for (var insideCounter = 0; insideCounter < $scope.gradebookContainer.assignmentAndAverageContainers.length; insideCounter++) {
                                     if ($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name === $scope.gradebookContainer.assignmentAndAverageContainers[insideCounter].assignment.name) {
                                         assignmentAveragesArray[counter] = $scope.gradebookContainer.assignmentAndAverageContainers[insideCounter].average;
                                         console.log("First average added for " + $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name + ": " + assignmentAveragesArray[counter]);
                                         if (assignmentAveragesArray[counter] === -1) {
                                             assignmentAveragesArray[counter] = "";
                                         }
                                     }
                                 }
                             }
                             $scope.assignmentAveragesArray = assignmentAveragesArray;
                        };

                                  console.log($scope.courseIdForGraph);

                                  var arraynum = new Array(1,2,3,4);
                                  var arraynum2 = new Array(4,3,2,1);




                   $scope.labels = ["January", "February", "March", "April", "May", "June", "July"];
                    $scope.series = ['Series A', 'Series B'];
                    $scope.data = [
                      $scope.assignmentAverages

                    ];
                    $scope.onClick = function (points, evt) {
                      console.log(points, evt);
                    };

                    // Simulate async data update
                    $timeout(function () {
                      $scope.data = [
                        $scope.assignmentAveragesArray


                      ];
                    }, 100);
                             
                             

}]);



      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
    
      
      
      
      
      
      
      
      
  

