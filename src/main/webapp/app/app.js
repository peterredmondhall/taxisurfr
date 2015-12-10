// create our angular app and inject ngAnimate and ui-router
// =============================================================================
angular.module('formApp', ['ngAnimate', 'ui.router', 'ui.bootstrap', 'angular-google-gapi', 'angularPayments'])


    // configuring our routes
    // =============================================================================
    .config(function ($stateProvider, $urlRouterProvider) {

        $stateProvider

        // route to show our basic form (/form)
            .state('form', {
                url: '/form',
                templateUrl: 'app/form.html',
                controller: 'formController'
            })

            // nested states
            // each of these sections will have their own view
            // url will be nested (/form/profile)
            .state('form.transport', {
                url: '/transport',
                templateUrl: 'app/form-transport.html'

            })

            .state('form.route', {
                url: '/route?route',
                templateUrl: 'app/form-transport.html',
                controller: function($scope, $stateParams) {
                    $state.routeId = $stateParams.route;
                }

            })

            // url will be /form/interests
            .state('form.details', {
                url: '/details',
                templateUrl: 'app/form-details.html'
            })

            // url will be /form/interests
            .state('form.summary', {
                url: '/summary',
                templateUrl: 'app/form-summary.html'
            })

            // url will be /form/payment
            .state('form.payment', {
                url: '/payment',
                templateUrl: 'app/form-payment.html'
            })

            // url will be /form/confirmation
            .state('form.confirmation', {
                url: '/confirmation',
                templateUrl: 'app/form-confirmation.html'
            })

        ;

        // catch all route
        // send users to the form page
        //$urlRouterProvider.otherwise('/form/transport');

        $urlRouterProvider.otherwise(function ($injector, $location) {
            var searchObject = $location.search().route;
            return '/form/transport';
        });
    })

    //.config(function (GApi) {
    //    // Simple GET request example:
    //    return GApi.execute('taxisurfr', 'session.get')
    //        .then(function (response) {
    //            Stripe.setPublishableKey(response.stripePublishable);
    //        });
    //})

    .run(['GAuth', 'GApi', 'GData', '$state', '$rootScope', '$window',
        function (GAuth, GApi, GData, $state, $rootScope, $window) {

            $rootScope.gdata = GData;

            var CLIENT = '526374069175-4vv42arm0ksdr9a1lgkve6vbktfkmlvv.apps.googleusercontent.com';
            if ($window.location.hostname == 'localhost') {
                BASE = '//localhost:8080/';
            } else {
                //BASE = 'https://taxigangsurf.appspot.com/_ah/api';
                BASE = 'https://gobygang.appspot.com/';
            }

            GApi.load('taxisurfr', 'v1', getBase($window) + "_ah/api");
            GApi.load('calendar', 'v3');
            GAuth.setClient(CLIENT);
            GAuth.setScope('https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/calendar.readonly');


            GApi.execute('taxisurfr', 'session.get')
                .then(function (response) {
                    Stripe.setPublishableKey(response.stripePublishable);
                });
        }
    ])

    // our controller for the form
    // =============================================================================
    .controller('formController', ['$scope', 'GApi', '$http', '$state', '$window',
        function ($scope, GApi, $http, $state, $window) {
            $scope.bookingPaid = false;

            //$scope.datePicker.date = {startDate: null, endDate: null};

            $scope.dateStatus = {
                opened: false
            };

            // we will store all of our form data in this object
            //$scope.cardData = {number: '4242 4242 4242 4242', name: 'Peter Hall', cvc: '123', expMonth: 09, expYear: 2019};

            $scope.booking = {};
            //    flightNo: 'EZ123',
            //    email: 'info@xyz.com',
            //    date: new Date(),
            //    name: 'Peter Hall',
            //    shareWanted: true,
            //    pickupTime: '12:00 pm',
            //    numPassengers: '1',
            //    numBoards: '2'
            //};
            if ($state.params.route){
                GApi.execute('taxisurfr', 'route.link', {routeId: $state.params.route}
                ).then(function (response) {
                    $scope.route = response;
                    $state.go('form.transport');
                });
            }
            //$scope.expMonth =  $scope.cardData.expMonth;
            //$scope.expYear = $scope.cardData.expYear;

            $scope.processing = false;
            $scope.inputready = true;

            //$scope.formData = {};
            $scope.getLocation = function (val) {
                if (val.length > 3 && $scope.inputready) {
                    $scope.inputready = false;
                    return GApi.execute('taxisurfr', 'routes.query', {query: val}
                    ).then(function (response) {
                        return response.items.map(function (item) {
                            $scope.inputready = true;
                            return item;
                        });
                    });
                }
            };

            // function to process the form

            $scope.processForm = function (status, response) {
                $scope.processing = true;
                if (response.error) {
                    // there was an error. Fix it.
                    $scope.paymentError = response.error.message;
                    $scope.processing = false;
                    throw new Error($scope.paymentError);
                } else {
                    // got stripe token, now charge it or smt
                    $scope.session = {cardToken: response.id, bookingId: $scope.booking.id};
                    GApi.execute('taxisurfr', 'booking.pay', $scope.session)
                        .then(function (response) {
                            $scope.processing = false;
                            $scope.booking = response;
                            if ($scope.booking.status === "PAID") {
                                $scope.bookingPaid = true;
                                $state.go('form.confirmation');
                            } else {
                                $scope.paymentError = $scope.booking.stripeRefusal;
                            }
                        });
                }
            };

            $scope.onSelect = function ($item, $model, $label) {
                $scope.route = $item;
                $scope.booking.route = $scope.route.id;
                $scope.imgSrc = getBase($window) + "imageservice?image=" + $scope.route.image;
            };

            $scope.addSession = function () {
                return GApi.execute('taxisurfr', 'session.new', $scope.route)
                    .then(function (response) {
                        $scope.session = response;
                    });
            };


            $scope.addBooking = function () {
                return GApi.execute('taxisurfr', 'booking.new', $scope.booking)
                    .then(function (response) {
                        $scope.booking = response;
                        console.log($scope.booking.id);
                    });
            };

            $scope.today = function() {
                $scope.dt = new Date();
            };
            $scope.today();

            $scope.clear = function () {
                $scope.dt = null;
            };

            $scope.toggleMin = function() {
                $scope.minDate = $scope.minDate ? null : new Date();
            };
            $scope.toggleMin();
            $scope.maxDate = new Date(2020, 5, 22);

            $scope.open = function($event) {
                $scope.status.opened = true;
            };

            $scope.setDate = function(year, month, day) {
                $scope.dt = new Date(year, month, day);
            };

            $scope.dateOptions = {
                formatYear: 'yy',
                startingDay: 1
            };

            $scope.status = {
                opened: false
            };

            var tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            var afterTomorrow = new Date();
            afterTomorrow.setDate(tomorrow.getDate() + 2);
            $scope.events =
                [
                    {
                        date: tomorrow,
                        status: 'full'
                    },
                    {
                        date: afterTomorrow,
                        status: 'partially'
                    }
                ];

            $scope.getDayClass = function(date, mode) {
                $scope.booking.dateText = new Date(date).setHours(0,0,0,0);
            };
        }
    ])

    //
    .controller('PaymentCtrl', ['$scope',
        function ($scope) {
            $scope.handleStripe = function (status, response) {
                if (response.error) {
                    // there was an error. Fix it.
                    console.log('error:' + response);
                } else {
                    // got stripe token, now charge it or smt
                    token = response.id;
                    console.log('success:' + token);
                }
            }
        }
    ])

    .factory('$exceptionHandler', ['$window',
        function ($window) {
            return function (exception, cause) {
                try {
                    var errorMessage = exception.toString();
                    var stackTrace = "stacktrace todo";
                    // use our traceService to generate a stack trace var stackTrace = traceService.print({e: exception});
                    // use AJAX (in this example jQuery) and NOT // an angular service such as $http
                    jQuery.ajax({
                        type: "POST",
                        url: "/taxisurfr/logging",
                        contentType: "application/json",
                        data: angular.toJson({
                            url: $window.location.href,
                            message: errorMessage,
                            type: "exception",
                            stackTrace: stackTrace,
                            cause: ( cause || "")
                        })
                    });
                } catch (loggingError) {
                    $log.warn("Error server-side logging failed");
                    $log.log(loggingError);
                }
            };
        }
    ])

;
var getBase = function (window) {
    if (window.location.hostname == 'localhost') {
        return '//localhost:8080/';
    } else {
        return 'https://taxigangsurf.appspot.com/';
        //return 'https://gobygang.appspot.com/';
    }
}

