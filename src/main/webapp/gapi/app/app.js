var app = angular.module('angular-google-api-example', [

    'ui.router',
    'angular-google-gapi',

    'angular-google-api-example.router',
    'angular-google-api-example.controller',


]);


app.run(['GAuth', 'GApi', 'GData', '$state', '$rootScope', '$window',
    function(GAuth, GApi, GData, $state, $rootScope, $window) {

        $rootScope.gdata = GData;

        var CLIENT = '526374069175-4vv42arm0ksdr9a1lgkve6vbktfkmlvv.apps.googleusercontent.com';
        var BASE;
        if($window.location.hostname == 'localhost') {
            BASE = '//localhost:8080/_ah/api';
        } else {
            BASE = 'https://cloud-endpoints-gae.appspot.com/_ah/api';
        }


        GApi.load('taxisurfr', 'v1', BASE);
        GAuth.setClient(CLIENT);
        GAuth.setScope('https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/calendar.readonly');
        GAuth.checkAuth().then(
            function () {
                if($state.includes('login'))
                    $state.go('home');
            },
            function() {
                $state.go('login');
            }
        );

        $rootScope.logout = function() {
            GAuth.logout().then(
            function () {
                $state.go('login');
            });
        };
    }
]);
