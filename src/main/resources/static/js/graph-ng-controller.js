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


                                   var buyerData = {
                                   	labels : ["January","February","March","April","May","June"],
                                   	datasets : [
                                   		{
                                   			fillColor : "rgba(172,194,132,0.4)",
                                   			strokeColor : "#ACC26D",
                                   			pointColor : "#fff",
                                   			pointStrokeColor : "#9DB86D",
                                   			data : [203,156,99,251,305,247]
                                   		}
                                   	]
                                   }


        console.log($scope.courseIdForGraph)
                            });

