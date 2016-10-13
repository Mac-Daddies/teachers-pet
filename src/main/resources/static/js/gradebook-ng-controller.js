angular.module('TeachersPetApp', [])
   .controller('GradebookController', function($scope, $http) {

        $scope.gradebook = function(courseId) {
            console.log("In gradebook function in ng controller with courseID = " + courseId);
            // go to new window
            $scope.currentClassId = courseId;

            $http.post("/gradebook.json", courseId)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        $scope.gradebookContainer = response.data;
                        // $scope.allAssignments = $scope.gradebookContainer.assignments;
                        $scope.allAssignmentsToGetLength = $scope.gradebookContainer.assignments;
                        $scope.allStudentAssignments = $scope.gradebookContainer.studentContainers.studentAssignments;
                        // $scope.numberOfAssignments = $scope.allAssignments.length;
                        $scope.numberOfAssignments = $scope.allAssignmentsToGetLength.length;

                        // new all assignments by getting out of studentAssignments list
                        $scope.allAssignments = new Array($scope.numberOfAssignments);



                        var currentStudentToGetAssignmentName;
                        for (var counter = 0; counter < $scope.numberOfAssignments; counter++) {
                            if (counter == 0) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (counter is 0)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            } else if (!(($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name) === ($scope.gradebookContainer.studentContainers[0].studentAssignments[counter - 1].assignment.name))) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (name isn't same as last)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            }

                        }

                        console.log("Printing out allAssignments:");
                        for (var index = 0; index < $scope.allAssignments.length; index++) {
                            console.log($scope.allAssignments[index]);
                        }

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.allGradebooks = function() {
            console.log("In allGradebooks function in ng controller");

            $http.post("/getAllClasses.json", $scope.teacherWhoIsLoggedIn)
                .then(
                    function successCallback(response) {
                        console.log("Response-- all classes: ");
                        console.log(response.data);
                        $scope.courseList = response.data;

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });


            $http.post("/allGradebooks.json", $scope.courseList)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        // FIX THIS PART!!!

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.addAssignment = function(newAssignmentName, newAssignmentDate) {
            console.log("In gradebook function in ng controller");

            var newAssignmentInfo = {
                name: newAssignmentName,
                dueDate: newAssignmentDate,
                course: $scope.currentClass
            }

            $http.post("/addAss.json", newAssignmentInfo)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");
                        // $scope.allAssignments = response.data;

                        $scope.gradebookContainer = response.data;
                        // $scope.allAssignments = $scope.gradebookContainer.assignments;
                        $scope.allAssignmentsToGetLength = $scope.gradebookContainer.assignments;
                        $scope.allStudentAssignments = $scope.gradebookContainer.studentContainers.studentAssignments;
                        // $scope.numberOfAssignments = $scope.allAssignments.length;
                        $scope.numberOfAssignments = $scope.allAssignmentsToGetLength.length;

                        // new all assignments by getting out of studentAssignments list
                        $scope.allAssignments = new Array($scope.numberOfAssignments);



                        var currentStudentToGetAssignmentName;
                        for (var counter = 0; counter < $scope.numberOfAssignments; counter++) {
                            if (counter == 0) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (counter is 0)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            } else if (!(($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name) === ($scope.gradebookContainer.studentContainers[0].studentAssignments[counter - 1].assignment.name))) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (name isn't same as last)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            }

                        }
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.addStudent = function(newStudentFirstName, newStudentLastName, newStudentParentEmail) {
            console.log("In addStudent function in ng controller");

            var newStudent = {
                firstName: newStudentFirstName,
                lastName: newStudentLastName,
                parentEmail: newStudentParentEmail
            }

            var newStudentInfoAndCourse = {
                student: newStudent,
                course: $scope.currentClass
            }

            $http.post("/addstudent.json", newStudentInfoAndCourse)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        $scope.gradebookContainer = response.data;
                        // $scope.allAssignments = $scope.gradebookContainer.assignments;
                        $scope.allAssignmentsToGetLength = $scope.gradebookContainer.assignments;
                        $scope.allStudentAssignments = $scope.gradebookContainer.studentContainers.studentAssignments;
                        // $scope.numberOfAssignments = $scope.allAssignments.length;
                        $scope.numberOfAssignments = $scope.allAssignmentsToGetLength.length;

                        // new all assignments by getting out of studentAssignments list
                        $scope.allAssignments = new Array($scope.numberOfAssignments);



                        var currentStudentToGetAssignmentName;
                        for (var counter = 0; counter < $scope.numberOfAssignments; counter++) {
                            if (counter == 0) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (counter is 0)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            } else if (!(($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name) === ($scope.gradebookContainer.studentContainers[0].studentAssignments[counter - 1].assignment.name))) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (name isn't same as last)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            }

                        }



                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.addGrades = function(currentAssignment, studentContainers) {
            console.log("In addGrade function in ng controller");
            console.log("Sending this list of student containers to backend:");
            console.log(studentContainers);
            console.log("Sending this assignment: ");
            console.log(currentAssignment);

            var addGradesContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            $http.post("/addGrade.json", addGradesContainer)
                .then(
                    function successCallback(response) {
                        console.log("This is what we get back: ");
                        console.log(response.data);
                        console.log("Adding data to scope");
                        $scope.updatedAssignmentGrades = response.data;
                        console.log("sending to backend...");

                        console.log($scope.updatedAssignmentGrades);

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.getNumberOfAssignments = function(num) {
            return new Array(num);
        }

        $scope.curveFlat = function(currentAssignment, studentContainers) {
            console.log("In curveFlat function in ng controller");

            curveContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            console.log("**About to send this currentAssignment: ");
            console.log(curveContainer.assignment);
            console.log("**About to send this list of StudentContainers:");
            console.log(curveContainer.studentContainers);

            $http.post("/curveFlat.json", curveContainer)
                .then(
                    function successCallback(response) {
                        console.log("**This is what we get back: ");
                        console.log(response.data);
                        console.log("Adding data to scope");

                        $scope.gradebookContainer = response.data;
                        // $scope.allAssignments = $scope.gradebookContainer.assignments;
                        $scope.allAssignmentsToGetLength = $scope.gradebookContainer.assignments;
                        $scope.allStudentAssignments = $scope.gradebookContainer.studentContainers.studentAssignments;
                        // $scope.numberOfAssignments = $scope.allAssignments.length;
                        $scope.numberOfAssignments = $scope.allAssignmentsToGetLength.length;

                        // new all assignments by getting out of studentAssignments list
                        $scope.allAssignments = new Array($scope.numberOfAssignments);



                        var currentStudentToGetAssignmentName;
                        for (var counter = 0; counter < $scope.numberOfAssignments; counter++) {
                            if (counter == 0) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (counter is 0)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            } else if (!(($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name) === ($scope.gradebookContainer.studentContainers[0].studentAssignments[counter - 1].assignment.name))) {
                                $scope.allAssignments[counter] = $scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
                                console.log("****In gradebook loop (name isn't same as last)**** (counter = " + counter + ") Assignment name added: ");
                                console.log($scope.gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                            }

                        }

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.curveAsPercentageOfHighestGrade = function(currentAssignment, studentContainers) {
            console.log("In curveAsPercentageOfHighestGrade function in ng controller");

            curveContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            console.log("**About to send this currentAssignment: ");
            console.log(curveContainer.assignment);
            console.log("**About to send this list of StudentContainers:");
            console.log(curveContainer.studentContainers);

            $http.post("/curveAsPercentageOfHighestGrade.json", curveContainer)
                .then(
                    function successCallback(response) {
                        console.log("**This is what we get back: ");
                        console.log(response.data);
                        console.log("Adding data to scope");
                        // var updatedAssignmentGrades = response.data;
                        // process updatedAssignmentGrades
                        $scope.updatedAssignmentGrades = response.data;
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };

        $scope.testMustacheVar = function() {
            console.log("testMustacheVar()");
            console.log("courseID: " + $scope.courseIdForGradebook);
        };


        var curveContainer;
//        console.log("I get executed without anyone calling me!!!");
//         console.log($scope.courseIdForGradebook);
//         $scope.gradebook($scope.courseIdForGradebook);
//         console.log($scope.courseIdForGradebook);
//         console.log(courseIdForGradebook);
//        var courseIdForGradebook = $scope.courseIdForGradebook;
        console.log($scope.courseIdForGradebook);
//         $scope.gradebook($scope.courseIdForGradebook);
//         console.log(courseIdForGradebook);



   });