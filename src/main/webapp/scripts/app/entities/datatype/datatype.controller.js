'use strict';

angular.module('piaApp')
    .controller('DatatypeController', function ($scope, $state, Datatype) {

        $scope.datatypes = [];
        $scope.loadAll = function() {
            Datatype.query(function(result) {
               $scope.datatypes = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.datatype = {
                name: null,
                description: null,
                id: null
            };
        };
    });