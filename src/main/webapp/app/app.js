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
                templateUrl: 'form.html',
                controller: 'formController'
            })

            // nested states
            // each of these sections will have their own view
            // url will be nested (/form/profile)
            .state('form.transport', {
                url: '/transport',
                templateUrl: 'form-transport.html'
            })

            // url will be /form/interests
            .state('form.details', {
                url: '/details',
                templateUrl: 'form-details.html'
            })

            // url will be /form/payment
            .state('form.payment', {
                url: '/payment',
                templateUrl: 'form-payment.html'
            });

        // catch all route
        // send users to the form page
        $urlRouterProvider.otherwise('/form/transport');
    })

    .config(function () {
        Stripe.setPublishableKey('pk_test_rcKuNpP9OpTri7twmZ77UOI5');
    })

    .run(['GAuth', 'GApi', 'GData', '$state', '$rootScope', '$window',
        function (GAuth, GApi, GData, $state, $rootScope, $window) {

            $rootScope.gdata = GData;

            var CLIENT = '526374069175-4vv42arm0ksdr9a1lgkve6vbktfkmlvv.apps.googleusercontent.com';
            var BASE;
            if ($window.location.hostname == 'localhost') {
                BASE = '//localhost:8080/_ah/api';
            } else {
                //BASE = 'https://cloud-endpoints-gae.appspot.com/_ah/api';
                BASE = 'https://taxigangsurf.appspot.com/_ah/api';
            }


            GApi.load('taxisurfr', 'v1', BASE);
            GApi.load('calendar', 'v3');
            GAuth.setClient(CLIENT);
            GAuth.setScope('https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/calendar.readonly');
        }
    ])

    // our controller for the form
    // =============================================================================
    .controller('formController', ['$scope', 'GApi', '$http',
        function ($scope, GApi, $state) {

           // we will store all of our form data in this object
            $scope.cardData = {number: '4242 4242 4242 4242', name: 'Peter Hall', cvc: '123', expMonth: 09, expYear: 2019};
            $scope.booking = {
                flightNo: 'EZ123',
                email: 'info@xyz.com',
                date: new Date(),
                name: 'Peter Hall',
                shareWanted: true,
                pickupTime: '12:00 pm',
                numPassengers: '1',
                numBoards: '2'
            };
            $scope.expMonth =  $scope.cardData.expMonth;
            $scope.expYear = $scope.cardData.expYear;

            //$scope.formData = {};
            $scope.getLocation = function (val) {
                if (val.length > 3) {
                    return GApi.execute('taxisurfr', 'routes.query', {query: val}
                    ).then(function (response) {
                        return response.items.map(function (item) {
                            return item;
                        });
                    });
                }
            };

            // function to process the form

            $scope.processForm = function (status, response) {
                if (response.error) {
                    // there was an error. Fix it.
                    console.log('error:' + response.error);
                } else {
                    // got stripe token, now charge it or smt
                    $scope.session.cardToken = response.id;
                    $scope.session.bookingId = $scope.booking.id;
                    GApi.execute('taxisurfr', 'booking.pay', $scope.session)
                        .then(function (response) {
                            $scope.booking = response;
                            if ($scope.booking.stripeRefusal == null) {
                                console.log($scope.booking.status);
                            }else{
                                console.log($scope.booking.stripeRefusal);
                            }
                        });
                }
            };

            $scope.onSelect = function ($item, $model, $label) {
                $scope.route = $item;
                $scope.booking.route = $scope.route.id;
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
            }

        }
    ])

    .controller('TypeaheadCtrl', ['$scope', 'GApi', '$state',
        function ($scope, GApi, $state) {

            $scope.selected = undefined;
            // Any function returning a promise object can be used to load values asynchronously

            $scope.getLocation = function (val) {
                if (val.length > 3) {
                    return GApi.execute('taxisurfr', 'routes.query', {query: val}
                    ).then(function (response) {
                        return response.items.map(function (item) {
                            return item;
                        });
                    });
                }
            };

            $scope.onSelect = function ($item, $model, $label) {
                $scope.$parent.route = $item;
            };
        }
    ])

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
    ]);
