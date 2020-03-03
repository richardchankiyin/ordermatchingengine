var com;
(function (com) {
    var richardchankiyin;
    (function (richardchankiyin) {
        var spreadcalc;
        (function (spreadcalc) {
            var SpreadRange = (function () {
                function SpreadRange(startFrom, endWith, spread) {
                    if (this.startFrom === undefined)
                        this.startFrom = 0;
                    if (this.endWith === undefined)
                        this.endWith = 0;
                    if (this.spread === undefined)
                        this.spread = 0;
                    if (startFrom <= 0)
                        throw Object.defineProperty(new Error("startFrom < 0"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    if (endWith <= 0)
                        throw Object.defineProperty(new Error("endWith < 0"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    if (spread <= 0)
                        throw Object.defineProperty(new Error("spread < 0"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    if (startFrom >= endWith)
                        throw Object.defineProperty(new Error("startWith: " + startFrom + " >= endWith: " + endWith), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    var noOfSpreadLong = Math.round((endWith - startFrom) / spread);
                    if (noOfSpreadLong < 1)
                        throw Object.defineProperty(new Error("noOfSpread supporting: " + noOfSpreadLong + " < l"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    var residual = (function (f1, f2) { var r = Math.abs(f1 % f2); if (isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) {
                        return r;
                    }
                    else {
                        return (f1 > 0 ? 1 : -1) * (r - f2);
                    } })((endWith - startFrom), spread);
                    if (!this.isLogicallyZero(residual))
                        throw Object.defineProperty(new Error("residual: " + residual + " > 0"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    this.startFrom = startFrom;
                    this.endWith = endWith;
                    this.spread = spread;
                }
                SpreadRange.prototype.getStartFrom = function () {
                    return this.startFrom;
                };
                SpreadRange.prototype.getEndWith = function () {
                    return this.endWith;
                };
                SpreadRange.prototype.getSpread = function () {
                    return this.spread;
                };
                /*private*/ SpreadRange.prototype.isLogicallyZero = function (num) {
                    return num < SpreadRange.VERY_LITTLE_NUM && num > SpreadRange.VERY_LITTLE_NUM * -1;
                };
                SpreadRange.prototype.isInRange = function (spot, isGoUp) {
                    if (isGoUp) {
                        if (!(spot < this.endWith && spot >= this.startFrom))
                            return false;
                    }
                    else {
                        if (!(spot <= this.endWith && spot > this.startFrom))
                            return false;
                    }
                    if (spot === this.startFrom || spot === this.endWith)
                        return true;
                    var residual = (function (f1, f2) { var r = Math.abs(f1 % f2); if (isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) {
                        return r;
                    }
                    else {
                        return (f1 > 0 ? 1 : -1) * (r - f2);
                    } })((spot - this.startFrom), this.spread);
                    return this.isLogicallyZero(residual);
                };
                SpreadRange.prototype.displace = function (spot, isGoUp, noOfSpreads) {
                    if (noOfSpreads < 1)
                        throw Object.defineProperty(new Error("noOfSpreads: " + noOfSpreads + " < 1"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    var isInRange = this.isInRange(spot, isGoUp);
                    if (isInRange === false)
                        throw Object.defineProperty(new Error("isInRange (spot: " + spot + " isGoUp:" + isGoUp + " not in range!"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    var result = 0;
                    if (isGoUp) {
                        result = spot + this.spread * noOfSpreads;
                    }
                    else {
                        result = spot - this.spread * noOfSpreads;
                    }
                    result = this.roundDouble(result);
                    if (result >= this.startFrom && result <= this.endWith) {
                        return result;
                    }
                    else {
                        throw Object.defineProperty(new Error("Result: " + result + " not in btw of start from: " + this.startFrom + " and end with: " + this.endWith), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    }
                };
                /*private*/ SpreadRange.prototype.roundDouble = function (input) {
                    return parseFloat(input.toFixed(5));
                };
                return SpreadRange;
            }());
            SpreadRange.VERY_LITTLE_NUM = 1.0E-7;
            SpreadRange.roundScale = 6;
            spreadcalc.SpreadRange = SpreadRange;
            SpreadRange["__class"] = "com.richardchankiyin.spreadcalc.SpreadRange";
            var SpreadRanges = (function () {
                function SpreadRanges() {
                    this.srn = null;
                    this.firstNode = null;
                    this.lastNode = null;
                    /*private*/ this.minPrice = 4.9E-324;
                    /*private*/ this.maxPrice = 1.7976931348623157E308;
                    this.srn = (function (s) { var a = []; while (s-- > 0)
                        a.push(null); return a; })(11);
                    this.srn[0] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(0.01, 0.25, 0.001));
                    this.srn[1] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(0.25, 0.5, 0.005));
                    this.srn[2] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(0.5, 10.0, 0.01));
                    this.srn[3] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(10.0, 20.0, 0.02));
                    this.srn[4] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(20.0, 100.0, 0.05));
                    this.srn[5] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(100.0, 200.0, 0.1));
                    this.srn[6] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(200.0, 500.0, 0.2));
                    this.srn[7] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(500.0, 1000.0, 0.5));
                    this.srn[8] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(1000.0, 2000.0, 1));
                    this.srn[9] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(2000.0, 5000.0, 2));
                    this.srn[10] = new SpreadRanges.SpreadRangeNode(this, new com.richardchankiyin.spreadcalc.SpreadRange(5000.0, 10000.0, 5));
                    this.firstNode = this.srn[0];
                    this.lastNode = this.srn[10];
                    this.minPrice = this.firstNode.getSpreadRange().getStartFrom();
                    this.maxPrice = this.lastNode.getSpreadRange().getEndWith();
                    for (var i = 0; i < this.srn.length; i++) {
                        {
                            var before = i - 1;
                            var after = i + 1;
                            if (before >= 0)
                                this.srn[i].setPrev(this.srn[before]);
                            if (after <= this.srn.length - 1)
                                this.srn[i].setNext(this.srn[after]);
                        }
                        ;
                    }
                }
                SpreadRanges.getInstance = function () {
                    if (SpreadRanges.instance == null) {
                        {
                            if (SpreadRanges.instance == null) {
                                SpreadRanges.instance = new SpreadRanges();
                                return SpreadRanges.instance;
                            }
                            else {
                                return SpreadRanges.instance;
                            }
                        }
                        ;
                    }
                    else {
                        return SpreadRanges.instance;
                    }
                };
                SpreadRanges.prototype.getSpreadPrices = function (spot, isGoUp, noOfSpreads) {
                    var node = this.getSpreadRangeNode(spot, isGoUp);
                    if (node == null) {
                        throw Object.defineProperty(new Error("invalid price: " + spot), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    }
                    if (noOfSpreads < 1) {
                        throw Object.defineProperty(new Error("noOfSpreads < 1"), '__classes', { configurable: true, value: ['java.lang.Throwable', 'java.lang.Object', 'java.lang.RuntimeException', 'java.lang.IllegalArgumentException', 'java.lang.Exception'] });
                    }
                    var result = (function (s) { var a = []; while (s-- > 0)
                        a.push(0); return a; })(noOfSpreads);
                    var currentSpot = spot;
                    var endIndex = 0;
                    var willContinue = true;
                    for (var i = 0; i < noOfSpreads && willContinue; i++) {
                        {
                            result[i] = node.getSpreadRange().displace(currentSpot, isGoUp, 1);
                            currentSpot = result[i];
                            endIndex = i;
                            if (!node.getSpreadRange().isInRange(currentSpot, isGoUp)) {
                                node = isGoUp ? node.getNext() : node.getPrev();
                                if (node == null)
                                    willContinue = false;
                            }
                        }
                        ;
                    }
                    if (endIndex < noOfSpreads - 1) {
                        var result2 = (function (s) { var a = []; while (s-- > 0)
                            a.push(0); return a; })(endIndex + 1);
                        for (var i = 0; i < endIndex + 1; i++) {
                            {
                                result2[i] = result[i];
                            }
                            ;
                        }
                        return result2;
                    }
                    else {
                        return result;
                    }
                };
                SpreadRanges.prototype.getSingleSpreadPrice = function (spot, isGoUp, noOfSpreads) {
                    var result = this.getSpreadPrices(spot, isGoUp, noOfSpreads);
                    var targetIndex = noOfSpreads - 1;
                    if (noOfSpreads > result.length) {
                        targetIndex = result.length - 1;
                    }
                    return result[targetIndex];
                };
                SpreadRanges.prototype.isValidPrice = function (price, isGoUp) {
                    return this.getSpreadRangeNode(price, isGoUp) != null;
                };
                SpreadRanges.prototype.getMinPrice = function () {
                    return this.minPrice;
                };
                SpreadRanges.prototype.getMaxPrice = function () {
                    return this.maxPrice;
                };
                SpreadRanges.prototype.getSpreadRangeNode = function (price, isGoUp) {
                    var startingNode = isGoUp ? this.firstNode : this.lastNode;
                    var result = false;
                    var willContinue = true;
                    do {
                        {
                            var sr = startingNode.getSpreadRange();
                            result = sr.isInRange(price, isGoUp);
                            if (result) {
                                willContinue = false;
                            }
                            else {
                                if (isGoUp) {
                                    if (price < sr.getEndWith()) {
                                        willContinue = false;
                                    }
                                    else {
                                        startingNode = startingNode.getNext();
                                        if (startingNode == null) {
                                            willContinue = false;
                                        }
                                    }
                                }
                                else {
                                    if (price > sr.getStartFrom()) {
                                        willContinue = false;
                                    }
                                    else {
                                        startingNode = startingNode.getPrev();
                                        if (startingNode == null) {
                                            willContinue = false;
                                        }
                                    }
                                }
                            }
                        }
                    } while ((willContinue));
                    return result ? startingNode : null;
                };
                return SpreadRanges;
            }());
            SpreadRanges.instance = null;
            spreadcalc.SpreadRanges = SpreadRanges;
            SpreadRanges["__class"] = "com.richardchankiyin.spreadcalc.SpreadRanges";
            (function (SpreadRanges) {
                var SpreadRangeNode = (function () {
                    function SpreadRangeNode(__parent, sr) {
                        this.__parent = __parent;
                        this.sr = null;
                        this.prev = null;
                        this.next = null;
                        this.sr = sr;
                    }
                    SpreadRangeNode.prototype.setPrev = function (prev) {
                        this.prev = prev;
                    };
                    SpreadRangeNode.prototype.setNext = function (next) {
                        this.next = next;
                    };
                    SpreadRangeNode.prototype.getSpreadRange = function () {
                        return this.sr;
                    };
                    SpreadRangeNode.prototype.getPrev = function () {
                        return this.prev;
                    };
                    SpreadRangeNode.prototype.getNext = function () {
                        return this.next;
                    };
                    return SpreadRangeNode;
                }());
                SpreadRanges.SpreadRangeNode = SpreadRangeNode;
                SpreadRangeNode["__class"] = "com.richardchankiyin.spreadcalc.SpreadRanges.SpreadRangeNode";
            })(SpreadRanges = spreadcalc.SpreadRanges || (spreadcalc.SpreadRanges = {}));
        })(spreadcalc = richardchankiyin.spreadcalc || (richardchankiyin.spreadcalc = {}));
    })(richardchankiyin = com.richardchankiyin || (com.richardchankiyin = {}));
})(com || (com = {}));
