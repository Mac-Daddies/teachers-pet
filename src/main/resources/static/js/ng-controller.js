angular.module('TeachersPetApp', [])
   .controller('SampleController', function($scope, $http) {


        $scope.home = function() {
            $scope.loginContainer = null;
        };


        $scope.login = function(loginEmail, loginPassword) {
            console.log("In login function in ng controller");

            var emailAndPass = {
                email: loginEmail,
                password: loginPassword
            }

            $http.post("/login.json", emailAndPass)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");
                        $scope.loginContainer = response.data;
                        if ($scope.loginContainer.errorMessage == null) {
                            $scope.loginSuccessful = true;
                            $scope.teacherWhoIsLoggedIn = $scope.loginContainer.teacher;
                            console.log("User who is logged in: " + $scope.teacherWhoIsLoggedIn.firstName + ", id: " + $scope.teacherWhoIsLoggedIn.id);
                            $scope.allCourses = $scope.loginContainer.courseArrayList;
                        } else {
                            $scope.loginSuccessful = false;
                        }
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.register = function(registerFirstName, registerLastName, registerEmail, registerPassword, registerSchool) {
                    console.log("In register function in ng controller");

                    var newUserInfo = {
                        firstName: registerFirstName,
                        lastName: registerLastName,
                        email: registerEmail,
                        password: registerPassword,
                        school: registerSchool
                    }

                    $http.post("/register.json", newUserInfo)
                        .then(
                            function successCallback(response) {
                                console.log(response.data);
                                console.log("Adding data to scope");
                                $scope.loginContainer = response.data;
                                if ($scope.loginContainer.errorMessage == null) {
                                    $scope.teacherWhoIsLoggedIn = $scope.loginContainer.teacher;
                                    console.log("User who is logged in: " + $scope.teacherWhoIsLoggedIn.firstName + ", id: " + $scope.teacherWhoIsLoggedIn.id);
                                    $scope.loginSuccessful = true;
                                } else {
                                    $scope.loginSuccessful = false;
                                }
                            },
                            function errorCallback(response) {
                                console.log("Unable to get data...");
                            });
                };


        $scope.addClass = function(newClassName, newClassSubject, newClassGradeLevel) {
            console.log("In addClass function in ng controller");

            var newClassInfo = {
                name: newClassName,
                subject: newClassSubject,
                gradeLevel: newClassGradeLevel,
                teacher: $scope.teacherWhoIsLoggedIn
            }

            $http.post("/addclass.json", newClassInfo)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");
                        $scope.allCourses = response.data;
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.gradebook = function(course) {
            console.log("In gradebook function in ng controller");
            $scope.currentClass = course;

            $http.post("/gradebook.json", course)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");
                        // ****This will have to change once we get the actual gradebook screen!
                        $scope.gradebookContainer = response.data;
                        $scope.allAssignments = $scope.gradebookContainer.assignmentArrayList;
                        $scope.allStudents = $scope.gradebookContainer.studentArrayList;
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
                        $scope.allAssignments = response.data;
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.addStudent = function(newStudentFirstName, newStudentLastName, newStudentParentEmail) {
            console.log("In addStudent function in ng controller");

            var newStudentInfo = {
                firstName: newStudentFirstName,
                lastName: newStudentLastName,
                parentEmail: newStudentParentEmail,
                course: $scope.currentClass
            }

            $http.post("/addstudent.json", newStudentInfo)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");
                        $scope.allStudents = response.data;
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };



   });