angular.module('TeachersPetApp',[])
    .controller('GraphController', function($scope, $http){

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



                                 },
                                   function errorCallback(response) {
                                    console.log("Unable to get data...");
                                });

                                getCurrentClass(courseId);
                                console.log("********* Got current class ***********");
                                console.log($scope.currentClass);


                                   };


        console.log($scope.courseIdForGraph)
                            });

