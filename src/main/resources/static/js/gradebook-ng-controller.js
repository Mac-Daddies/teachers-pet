//Gradebook angular controller
angular.module('TeachersPetApp', ["chart.js"])
    .config(['ChartJsProvider', function (ChartJsProvider) {
        // Configure all charts
        ChartJsProvider.setOptions({
          chartColors: ['#FF5252', '#4286f4'],
          responsive: false
        });
        // Configure all line charts
        ChartJsProvider.setOptions('line', {
          showLines: true
        });
      }])
   .controller('GradebookController', function($scope, $http, $window, $timeout) {

        var getCurrentClass = function(courseId) {
            console.log("In getCurrentClass function in ng controller with courseID = " + courseId);

            $http.post("/getCurrentClass.json", courseId)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        $scope.currentClass = response.data;


                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };

        $scope.logout = function() {
            $window.location.href = '/';
        }

        $scope.ngBack =function() {
             $window.location.href = '/classList?teacherId=' + $scope.currentClass.teacher.id;
       };


        $scope.gradebook = function(courseId) {
            console.log("In gradebook function in ng controller with courseID = " + courseId);

            $scope.currentClassId = courseId;

            $http.post("/gradebook.json", courseId)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        fillGradebookContainerWithResponseData(response.data);

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });

                    getCurrentClass(courseId);
                    console.log("********* Got current class ***********");
                    console.log($scope.currentClass);


        };

        var fillGradebookContainerWithResponseData = function(responseData) {

            var gradebookContainer = responseData;
            if(gradebookContainer.studentContainers.length > 0) {
                $scope.showAddAssignmentButton = true;
            }
            var allAssignmentsToGetLength = gradebookContainer.assignmentAndAverageContainers;
//                        console.log("****Here is allAssignmentsToGetLength - check to make sure all in there");
            console.log(allAssignmentsToGetLength);
            var allStudentAssignments = gradebookContainer.studentContainers.studentAssignments;
            var numberOfAssignments = allAssignmentsToGetLength.length;

            // new all assignments by getting out of studentAssignments list
            var allAssignments = new Array(numberOfAssignments);

            //if any of the grades are -1, we need to change them to not showing!! N/A for now.
            for (var counter = 0; counter < gradebookContainer.studentContainers.length; counter++) {
                for (var insideCounter = 0; insideCounter < gradebookContainer.studentContainers[counter].studentAssignments.length; insideCounter++) {
                    if (gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].grade === -1) {
                        //replace -1 with blank
//                        console.log("!!!!!!!!!!CHANGING -1 GRADE TO BLANK!!!!!!!!!!");
                        gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].grade = "";
//                        console.log(gradebookContainer.studentContainers[counter].studentAssignments[insideCounter].grade);
                    }
                }
            }

            //loop to populate allAssignments array in the order that the grades are being displayed (studentAssignments are ordered by date assignment on the back end)
            for (var counter = 0; counter < numberOfAssignments; counter++) {
                if (counter == 0) {
                    allAssignments[counter] = gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
//                                console.log("****In gradebook loop (counter is 0)**** (counter = " + counter + ") Assignment name added: ");
                    console.log(gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                } else if (!((gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name) === (gradebookContainer.studentContainers[0].studentAssignments[counter - 1].assignment.name))) {
                    allAssignments[counter] = gradebookContainer.studentContainers[0].studentAssignments[counter].assignment;
//                                console.log("****In gradebook loop (name isn't same as last)**** (counter = " + counter + ") Assignment name added: ");
                    console.log(gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name);
                }

            }

            //Format the date correctly for each assignment (make sure to put formatting back before sending back to backend)
            for (var counter = 0; counter < allAssignments.length; counter++) {
                var dueDate = allAssignments[counter].dueDate;
                var dateString = dueDate.substring(5, 7) + "/" + dueDate.substring(8, 10) + "/" + dueDate.substring(0, 4);
                allAssignments[counter].dueDate = dateString;
            }

            //Don't display average (-1) for students who have no grades entered yet
            for (var counter = 0; counter < gradebookContainer.studentContainers.length; counter++) {
                if (gradebookContainer.studentContainers[counter].average === -1) {
                    gradebookContainer.studentContainers[counter].average = "";
                }
            }

            var assignmentAveragesContainerArray = new Array(numberOfAssignments);
            //loop to populate assignmentAverages array in the order that the grades are being displayed
            console.log("******** Here is the assignment and average containers:");
            console.log(gradebookContainer.assignmentAndAverageContainers);
            for (var counter = 0; counter < numberOfAssignments; counter++) {
                for (var insideCounter = 0; insideCounter < gradebookContainer.assignmentAndAverageContainers.length; insideCounter++) {
                    if (gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name === gradebookContainer.assignmentAndAverageContainers[insideCounter].assignment.name) {
                        assignmentAveragesContainerArray[counter] = gradebookContainer.assignmentAndAverageContainers[insideCounter];
                        console.log("First average (current) added for " + gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name + ": " + assignmentAveragesContainerArray[counter].currentAverage);
                        console.log("First average (original) added for " + gradebookContainer.studentContainers[0].studentAssignments[counter].assignment.name + ": " + assignmentAveragesContainerArray[counter].originalAverage);
                        if (assignmentAveragesContainerArray[counter].originalAverage === -1) {
                            assignmentAveragesContainerArray[counter].originalAverage = "";
                        }
                        if (assignmentAveragesContainerArray[counter].currentAverage === -1) {
                            assignmentAveragesContainerArray[counter].currentAverage = "";
                        }
                    }
                }
            }

            $scope.assignmentAveragesContainerArray = assignmentAveragesContainerArray;

            $scope.allAssignments = allAssignments;
            $scope.numberOfAssignments = numberOfAssignments;
            $scope.gradebookContainer = gradebookContainer;
            $scope.highAverage = gradebookContainer.highAverage;

       };


        var populateBlankGradesWithNegativeOnesBeforeSending = function (studentContainers) {
            console.log("In populateBlankGrades... method in gradebook-ng-controller");
            console.log("Grades right now:");
            console.log(studentContainers);
            for (var counter = 0; counter < studentContainers.length; counter++) {
                for (var insideCounter = 0; insideCounter < studentContainers[counter].studentAssignments.length; insideCounter++) {
                    if (studentContainers[counter].studentAssignments[insideCounter].grade === "") {
                        console.log("Grade is empty, changing to -1 before sending back.");
                        studentContainers[counter].studentAssignments[insideCounter].grade = -1;
                    }
//                    var dueDate = studentContainers[counter].studentAssignments[insideCounter].assignment.dueDate;
//                    var dateString = dueDate.substring(6, 10) + "-" + dueDate.substring(0, 2) + "-" + dueDate.substring(3, 5) + "T04:00:00.000Z";
                    console.log("dueDate for " + studentContainers[counter].studentAssignments[insideCounter].assignment.name + " is " + studentContainers[counter].studentAssignments[insideCounter].assignment.dueDate);
//                    console.log("dueDate for " + studentContainers[counter].studentAssignments[insideCounter].assignment.name + " is now " + dateString);
                }

                if (studentContainers[counter].average === "") {
                	studentContainers[counter].average = -1;
                }
            }
            console.log("Updated version to send to backend:");
            console.log(studentContainers);
        };


        var populateBlankGradesWithNegativeOnesBeforeSendingSingleStudent = function (studentContainer) {
            console.log("In populateBlankGrades...SingleStudent method in gradebook-ng-controller");
            console.log("Grades right now:");
            console.log(studentContainer);
            for (var counter = 0; counter < studentContainer.studentAssignments.length; counter++) {
                if (studentContainer.studentAssignments[counter].grade === "") {
                    console.log("Grade is empty, changing to -1 before sending back.");
                    studentContainer.studentAssignments[counter].grade = -1;
                }
//                    var dueDate = studentContainers[counter].studentAssignments[insideCounter].assignment.dueDate;
//                    var dateString = dueDate.substring(6, 10) + "-" + dueDate.substring(0, 2) + "-" + dueDate.substring(3, 5) + "T04:00:00.000Z";
                console.log("dueDate for " + studentContainer.studentAssignments[counter].assignment.name + " is " + studentContainer.studentAssignments[counter].assignment.dueDate);
//                    console.log("dueDate for " + studentContainers[counter].studentAssignments[insideCounter].assignment.name + " is now " + dateString);
            }

            if (studentContainer.average === "") {
            	studentContainer.average = -1;
            }

            console.log("Updated version to send to backend:");
            console.log(studentContainer);
        };


        $scope.addAssignment = function(newAssignmentName, newAssignmentDate) {
            console.log("In gradebook function in gradebook-ng-controller");

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

                        fillGradebookContainerWithResponseData(response.data);
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };

        $scope.deleteAssignment = function(assignment) {
            console.log("In deleteAssignment function in gradebook-ng-controller");

            $http.post("/deleteAss.json", assignment)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        fillGradebookContainerWithResponseData(response.data);
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

                        fillGradebookContainerWithResponseData(response.data);
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });

        };

        $scope.deleteStudent = function(student) {
            console.log("In deleteStudent function in gradebook-ng-controller");

            $http.post("/deleteStudent.json", student)
                .then(
                    function successCallback(response) {
                        console.log(response.data);
                        console.log("Adding data to scope");

                        fillGradebookContainerWithResponseData(response.data);
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };
         $scope.switchtogradeandgraph = function(courseId) {
                    console.log("In joint function in ng controller");
                    // go to new window
                    $scope.currentClassid = courseId;

                    $window.location.href = '/gradebookandgraph?courseId=' + courseId;
                };


        $scope.addGrades = function(currentAssignment, studentContainers) {
            console.log("In addGrades function in ng controller");
            populateBlankGradesWithNegativeOnesBeforeSending(studentContainers);

            var addGradesContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            console.log("These are the grades I'm sending through:");
            for (var counter = 0; counter < addGradesContainer.studentContainers.length; counter++) {
                for (var insideCounter = 0; insideCounter < addGradesContainer.studentContainers[counter].studentAssignments.length; insideCounter++) {
                    console.log(addGradesContainer.studentContainers[counter].student.firstName + "'s grade on " + addGradesContainer.studentContainers[counter].studentAssignments[insideCounter].assignment.name + ": " + addGradesContainer.studentContainers[counter].studentAssignments[insideCounter].grade);
                }
            }

            $http.post("/addGrades.json", addGradesContainer)
                .then(
                    function successCallback(response) {
                        console.log("This is what we get back: ");
                        console.log(response.data);
                        console.log("Adding data to scope");
                        fillGradebookContainerWithResponseData(response.data);
                        $scope.showGraph(currentAssignment);
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.getNumberOfAssignments = function(num) {
            return new Array(num);
        }

        $scope.addExtraCredit = function(extraCreditAmount, currentAssignment, studentContainers) {
            console.log("In addExtraCredit function in ng controller");

            // first send the grades that are in there (for if the user altered data, then pressed add extra credit w/o saving grades first)
//            sendGradesFromTable(currentAssignment, studentContainers);
            console.log("Adding grades first...");
            populateBlankGradesWithNegativeOnesBeforeSending(studentContainers);

            var addGradesContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }


            $http.post("/addGrades.json", addGradesContainer)
                .then(
                    function successCallback(response) {

                       curveContainer = {
                            extraCreditAmount: extraCreditAmount,
                            assignment: currentAssignment,
                            studentContainers: studentContainers
                        }

                        $http.post("/addExtraCredit.json", curveContainer)
                            .then(
                                function successCallback(response) {
            //                        console.log("**This is what we get back: ");
                                    console.log(response.data);
                                    console.log("Adding data to scope");
                                    fillGradebookContainerWithResponseData(response.data);
                                    $scope.showGraph(currentAssignment);
                                    extraCreditAmount = 0;
                                    $scope.extraCreditAmount = 0;
                                    $scope.extraCreditAmount = extraCreditAmount;
                                },
                                function errorCallback(response) {
                                    console.log("Unable to get data at addExtraCredit endpoint...");
                                });

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data at addGrades endpoint...");
                    });


        };


        $scope.curveFlat = function(currentAssignment, studentContainers) {
            console.log("In curveFlat function in ng controller");

            console.log("Adding grades first...");
            populateBlankGradesWithNegativeOnesBeforeSending(studentContainers);

            var addGradesContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            $http.post("/addGrades.json", addGradesContainer)
                .then(
                    function successCallback(response) {

                         curveContainer = {
                             assignment: currentAssignment,
                             studentContainers: studentContainers
                         }

                         $http.post("/curveFlat.json", curveContainer)
                             .then(
                                 function successCallback(response) {
                                     console.log(response.data);
                                     console.log("Adding data to scope");

                                     fillGradebookContainerWithResponseData(response.data);
                                     $scope.showGraph(currentAssignment);

                                 },
                                 function errorCallback(response) {
                                     console.log("Unable to get data at curveFlat endpoint...");
                                 });

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data at addGrades endpoint...");
                    });

        };


        $scope.curveAsPercentageOfHighestGrade = function(currentAssignment, studentContainers) {
            console.log("In curveAsPercentageOfHighestGrade function in ng controller");

            console.log("Adding grades first...");
            populateBlankGradesWithNegativeOnesBeforeSending(studentContainers);

            var addGradesContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            $http.post("/addGrades.json", addGradesContainer)
                .then(
                    function successCallback(response) {

                        curveContainer = {
                            assignment: currentAssignment,
                            studentContainers: studentContainers
                        }

                        $http.post("/curveAsPercentageOfHighestGrade.json", curveContainer)
                            .then(
                                function successCallback(response) {
                                    console.log(response.data);
                                    console.log("Adding data to scope");
                                    fillGradebookContainerWithResponseData(response.data);
                                    $scope.showGraph(currentAssignment);
                                },
                                function errorCallback(response) {
                                    console.log("Unable to get data at curveAsPercentageOfHighestGrade endpoint...");
                                });

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data at addGrades endpoint...");
                    });

        };


        $scope.curveByTakingRoot = function(currentAssignment, studentContainers) {
            console.log("In curveByTakingRoot function in ng controller");

            console.log("Adding grades first...");
            populateBlankGradesWithNegativeOnesBeforeSending(studentContainers);

            var addGradesContainer = {
                assignment: currentAssignment,
                studentContainers: studentContainers
            }

            $http.post("/addGrades.json", addGradesContainer)
                .then(
                    function successCallback(response) {

                        curveContainer = {
                            assignment: currentAssignment,
                            studentContainers: studentContainers
                        }

                        $http.post("/curveByTakingRoot.json", curveContainer)
                            .then(
                                function successCallback(response) {
                                    console.log(response.data);
                                    console.log("Adding data to scope");
                                    fillGradebookContainerWithResponseData(response.data);
                                    $scope.showGraph(currentAssignment);
                                },
                                function errorCallback(response) {
                                    console.log("Unable to get data at curveByTakingRoot endpoint...");
                                });

                    },
                    function errorCallback(response) {
                        console.log("Unable to get data at addGrades endpoint...");
                    });

        };

        $scope.getOriginalGrades = function(currentAssignment) {
              console.log("In getOriginalGrades function in gradebook-ng-controller");

              $http.post("/getOriginalGrades.json", currentAssignment)
                  .then(
                      function successCallback(response) {
                          console.log(response.data);
                          console.log("Adding data to scope");
                          fillGradebookContainerWithResponseData(response.data);
                          $scope.showGraph(currentAssignment);
                      },
                      function errorCallback(response) {
                          console.log("Unable to get data...");
                      });
        };


        $scope.sendEmailOneStudent = function(studentContainer) {
            console.log("In sendEmailOneStudent function in gradebook-ng-controller");

            populateBlankGradesWithNegativeOnesBeforeSendingSingleStudent(studentContainer);

            if (studentContainer.average != -1) {
                $http.post("/sendEmailOneStudent.json", studentContainer)
                    .then(
                        function successCallback(response) {
    //                        console.log("**This is what we get back: ");
                            console.log(response.data.message);
                            console.log("Adding data to scope");
                            fillGradebookContainerWithResponseData($scope.gradebookContainer);
                            $window.alert(response.data.message);
                        },
                        function errorCallback(response) {
                            console.log("Unable to get data...");
                        });
            } else {
                $window.alert("Email could not be sent because there is no grade data for this student yet!");
                fillGradebookContainerWithResponseData($scope.gradebookContainer);
            }
        };

        $scope.sendEmailForAllZeros = function() {
            console.log("In sendEmailForAllZeros function in gradebook-ng-controller");

            curveContainer = {
                assignment: null,
                studentContainers: $scope.gradebookContainer.studentContainers
            }

            $http.post("/sendEmailForAllZeros.json", curveContainer)
                .then(
                    function successCallback(response) {
                        console.log(response.data.message);
                        console.log("Adding data to scope");
                        $window.alert(response.data.message);
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


        $scope.sendEmailForAllHighAverages = function() {
            console.log("In sendEmailForAllHighAverages function in gradebook-ng-controller");

            curveContainer = {
                assignment: null,
                studentContainers: $scope.gradebookContainer.studentContainers
            }

            $http.post("/sendEmailForAllHighAverages.json", curveContainer)
                .then(
                    function successCallback(response) {
                        console.log(response.data.message);
                        console.log("Adding data to scope");
                        $window.alert(response.data.message);
                    },
                    function errorCallback(response) {
                        console.log("Unable to get data...");
                    });
        };


    var square, rightone;

    square = document.getElementById("square"),

    rightone = document.getElementById("rightone"),

        clickMe = document.getElementById("clickMe");


    function split () {

             rightone.style.display = "inherit";
             square.style.width = "35%";
             square.style.display = "inherit";
             rightone.style.right = "40%";
             square.style.position = "absolute";

             rightone.style.position = "fixed";

             right1.style.display = "none";

    }


    clickMe.onclick = split

    var right1 = document.getElementById("right"),

       clickMe1 = document.getElementById("clickMe1");


        function book () {
                 square.style.width = "100%";
                 square.style.display = "inherit";
                 square.style.position = "relative";
                 right.style.display= "none";
                 rightone.style.display= "none";
        }

         clickMe1.onclick = book

         var right1 = document.getElementById("right"),
          gb = document.getElementById("gb"),
          group = document.getElementById("group"),


          clickMe2 = document.getElementById("clickMe2");


/*****************************/
       function graph () {
            square.style.display = "none";

            rightone.style.display = "none";

            right1.style.position = "absolute";
            right1.style.width = "100%";
            right1.style.height = "100%";
            right1.style.left = "0";
            right1.style.right = "0";
            right1.style.top = "2%";

            right1.style.display = "inline";

            gb.style.display = "flex";

            group.style.display = "inherit";


            getCorrectAverage($scope.assforgraph);

       }

       clickMe2.onclick = graph

        var squares, rightones;

           squares = document.getElementById("square"),

           rightones = document.getElementById("rightone"),

               clickThis = document.getElementById("clickThis");

           function splitView () {

             rightones.style.display = "inherit";
             squares.style.width = "35%";
             squares.style.display = "inherit";
             rightones.style.right = "40%";
             squares.style.position = "absolute";

             rightones.style.position = "fixed";



             right.style.display = "none";

           }


           clickThis.onclick = splitView

           var right1 = document.getElementById("right"),

              clickThis1 = document.getElementById("clickThis1");


               function booked () {

                 squares.style.width = "100%";
                 squares.style.display = "inherit";
                 squares.style.position = "relative";
                 right1.style.display= "none";
                 rightones.style.display= "none";

               }

                clickThis1.onclick = booked



       $scope.showGraph = function(assignment) {
            console.log("In showGraph function in gradebook-ng-controller");
            console.log("Assignment: " + assignment.name);


            $http.post("/graphIndividualAssignment.json", assignment)
                .then(
                    function successCallback(response) {
                        //the endpoint will send back:
                        // (1) an arraylist of percentages of students who got in each range (for original grades)
                        // (2) an arraylist of percentages of students who got in each range (for current grades)
                        console.log(response.data);
                        console.log("Adding data to scope");
                        gradeDataForTable = response.data;

                        updateGraph(assignment);

                },
                function errorCallback(response) {
                    console.log("Unable to get data...");
                });
            };

            $scope.swapStyle = function(sheet){
                    document.getElementById('pagestyle').setAttribute('href',sheet);
            }

        var getCorrectAverage = function(assignment) {
            for (var counter = 0; counter < $scope.gradebookContainer.assignmentAndAverageContainers.length; counter++) {
                if ($scope.gradebookContainer.assignmentAndAverageContainers[counter].assignment.name === assignment.name) {
                    $scope.originalAverageForGraphView = $scope.gradebookContainer.assignmentAndAverageContainers[counter].originalAverage;
                    $scope.currentAverageForGraphView = $scope.gradebookContainer.assignmentAndAverageContainers[counter].currentAverage;
                }
            }
        };

        var updateGraph = function(assignment) {
            console.log("In updateGraph function in gradebook-ng-controller");
            console.log("Did gradeDataForTable come through?");
            console.log(gradeDataForTable);
            getCorrectAverage(assignment);
            var gradeCategories = ["0-9", "10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99", "100+"];

            $scope.labels = gradeCategories;

            $scope.series = ['Original Grades', 'Curved/Modified Grades'];
            $scope.data = [
                gradeDataForTable.percentagesOfOriginalGrades,
                gradeDataForTable.percentagesOfCurvedGrades
            ];

              $scope.onClick = function (points, evt) {
                console.log(points, evt);
              };
              $scope.datasetOverride = [
                    {
                      yAxisID: 'y-axis-1'
                    },
                    {
                      xAxisID: 'x-axis-1'
                    }

                  ];
              $scope.options = {
                title: {
                    display: true,
                    text: 'Grade Distribution Graph for ' + assignment.name
                },
                legend: {
                    display: true,
                    position: 'top'
                },
                scales: {
                  yAxes: [
                    {
                      id: 'y-axis-1',
                      type: 'linear',
                      display: true,
                      position: 'left',
                      ticks: {
                        min: 0,
                        max: 100,
                        beginAtZero: true
                      },
                      scaleLabel: {
                              display: true,
                              labelString: 'Percentage of students'
                      }
                    }
                  ],
                  xAxes: [
                      {
                        id: 'x-axis-1',
                        display: true,
                        position: 'bottom',
                        scaleLabel: {
                                display: true,
                                labelString: 'Grade on assignment'
                        }
                      }
                    ]
                }
              };


            $scope.graphShowing = true;
            $scope.assforgraph = assignment;
        };
        $scope.hideGraph = function(){
            $scope.graphShowing = false;
        }


        var curveContainer;

        // This is undefined here, so we made ng-init at top to call gradebook with courseId from mustache
        console.log($scope.courseIdForGradebook);
        var gradeDataForTable;
        var isGradeDataDoneSaving;
        $scope.showAddAssignmentButton = false;


   });