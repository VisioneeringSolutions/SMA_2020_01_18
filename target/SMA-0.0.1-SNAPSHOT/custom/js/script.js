
/**
 * Created by SumitJangir on 6/5/16.
 */


(function (window, document, angular, undefined) {

    var localStorage = new (function (ls, enc, dec) {
        var isbase64Available = enc ? true : false;
        this.add = function (key, val) {
            try {
                ls[key] = isbase64Available ? enc(val) : val;
            } catch (err) {
                console.log(err);
                ls[key] = val;
            }

            return ls[key];
        };
        this.remove = function (key) {
            if (ls[key])
                delete ls[key];
            return ls[key];
        };
        this.get = function (key) {
            try {
                return isbase64Available ? dec(ls[key]) : ls[key];
            } catch (err) {
                //console.log(err);
                return ls[key];
            }

        };
    })(window.localStorage, window.btoa, window.atob);

    var pageLoader = function () {
        this.start = function () {
            document.querySelector('.main-page-load').style.display = "block";
        };
        this.stop = function () {
           document.querySelector('.main-page-load').style.display = "none";
        };
        this.isStarted = function () {
            var res = document.querySelector('.main-page-load').style.display;
            return (res != "none") ? true : false;
        };
    };

    function EventManager() {
        var events = {};
        var cache = {};
        this.get = function (key) {
            return cache[key];
        };
        this.set = function (key, value) {
            if (cache[key] != value) {
                var old = cache[key];
                cache[key] = value;
                if (events[key])
                    events[key](value, old);
            }

            //console.log("cache set ",cache);
            //console.log("event cache set ",events);
        };
        this.remove = function (key) {
            delete cache[key];
            // console.log("cache cache remove ",cache);
            //console.log("event cache remove ",events);
        };
        this.on = function (key, value) {
            events[key] = value;
            //console.log("cache event on ",cache);
            //console.log("events event on ",events);
        };
        this.off = function (key) {
            delete events[key];
            //console.log("cache event off ",cache);
            //console.log("events event off ",events);
        };
        this.call = function (key) {
            //console.log("events call",events);
            if (events[key]) {
                events[key](arguments);
            }

            //console.log("cache event call ",cache);
            //console.log("events event call ",events);
        };
    };

    function noDblDigitFormat(n) {
        return n > 9 ? "" + n : "0" + n;
    }

    var coreWorkerInstance;

    function CoreWorker() {
        var workers = {};

        this.createWorker = function (workerPath) {
            var w = new Worker(workerPath);
            var k = parseInt(Math.random() * 1000);
            w.uKey = k;
            workers[k] = w;
            return workers[k];
        };
        this.destroy = function (key) {
            if (workers[key]) {
                try {
                    workers[key].terminate();
                    delete workers[key];
                } catch (err) {

                }
                return true;

            }
            return false;
        };
        this.get = function (key) {
            if (workers[key])
                return workers[key];
            return workers;
        };

    }



    /*disable scroll wheel lock defining*/
    $(document).on("wheel", "input[type=number]", function (e) {
        $(this).blur();
    });
    /* disable arrow keys lock defining*/
    $(document).on('keydown', 'input[type=number]', function (e) {
        if (e.which == 38 || e.which == 40)
            e.preventDefault();
    });



    //console.log(window);
    /* window.addEventListener("beforeunload", function (e) {
         //console.log("going to beforeunload", e);
         e.preventDefault();
         return false;
     });*/

    /*window.onbeforeunload = function(e) {
     console.log("going to onbeforeunload",e);
     e.preventDefault();
     return false;
     };*/

    /*window.addEventListener('unload', function (e) {
        //console.log("going to unload", e);
        e.preventDefault();
        return false;
    });
    window.addEventListener('load', function (e) {
        //console.log("going to load", e);
        e.preventDefault();
        return false;
    });*/
    /*window.addEventListener('loadeddata',function(e){
     console.log("going to onloadeddata",e);
     });
     window.addEventListener('loadedmetadata',function(e){
     console.log("going to onloadedmetadata",e);
     });
     window.addEventListener('loadstart',function(e){
     console.log("going to onloadstart",e);
     });
     window.addEventListener('loadeddata',function(e){
     console.log("going to onloadeddata",e);
     });*/

    /*window.onpopstate = function(event) {
     console.log("location: " + document.location + ", state: " + JSON.stringify(event.state));
     };*/

    // To disable f5
    /*function disableF5(e) {
        //console.log("KeyCode", e.keyCode);
        //console.log("Which", e.which);
        if ((e.which || e.keyCode) == 116) {
            e.preventDefault();
        }
    };

    $(document).bind("keydown", disableF5);
    $(document).on("keydown", disableF5);*/

// To disable right click
    /*window.oncontextmenu = function () {
        return false;
    };*/

    //window.open("statusbar=0,menubar=0,toolbar=1");
    //window.history.forward(-1);

    var sessionArray = [];
    var today = new Date();

    for (var i = 0; i < 3; i++) {
        var session = null;
        var session2 = null;
        var curr = 0, next = 0;
        if (today.getMonth() >= 3) {
            curr = today.getFullYear();
            next = today.getFullYear() + 1;
        }
        else {
            curr = today.getFullYear() - 1;

            next = today.getFullYear();
        }
        next = next - parseInt(i);
        curr = curr - parseInt(i);
        session = curr + "-" + next;
        session2 = curr + "-" + curr;
        sessionArray.push(session);
        sessionArray.push(session2);
    }


    var yearArray = [];
    for (var i = -1; i < 2; i++) {
        var year = today.getFullYear();
        year = year - parseInt(i);
        yearArray.push(year+"");
    }


    window.sInstances = {
        EventManager: EventManager,
        pageLoader: pageLoader,
        noDblDigitFormat: noDblDigitFormat,
        localStorage: localStorage,
        origin: window.location.origin,
        worker: (new CoreWorker()),
        sessionArray: sessionArray,
        yearArray: yearArray
    };

    //$(document).ready(function() {
    //    var counter = 0;
    //    toggleRedClass();
    //
    //    function toggleRedClass(){
    //        var boxes = $('.toggle-tab ');
    //        console.log("box length::",boxes.length)
    //    }
    //})
})(window, window.document, window.angular);
