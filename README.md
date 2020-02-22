# Order Matching Engine
This is a repository about the implementation of order matching engine written in java

Requirements
=============
Order validation
-----------------
1. Only orders with buy/sell limit/market orders will be accepted. An order needs to have ClOrdID (FIX Tag 11), MsgType (FIX Tag 35), Side (FIX Tag54), Symbol (FIX Tag 55), OrdType (FIX Tag 40), Price (FIX Tag 44 only required in limit) and OrderQty (FIX Tag 38).

2. Order amendment is being supported with limited manner. Only amend down order (Tag 38) with value less than original one will be accepted. Otherwise orders will be rejected. For others like amend up/change price orders, cancel and new orders done in two steps required

3. Order replace request/cancel can be done anytime except order is pending new, suspended (pending execution), or filled

4. Order price must be a valid price from spread range calculation

5. Order will be rejected if pending new queue is full.

Order state
------------
1. Order status (FIX Tag 39) NewOrderSingle: PendingNew (A) -> New (0)
                                                            -> Rejected (8)
2. Order status (replace request): New (0) <-> Suspended (9) -> PartiallyFilled (1) -> Suspended (9) -> Filled (2) -> Done For Day (3) 
3. Order status (cancellation): New (0) -> Cancelled (4) <br/> 
                                PartialFilled (1) -> Filled (2)

<br/>
Remarks: for 2, we do not have a state called "Replaced" (5) maintained internally in the state machine because it will be very complicated. For orders accepted without execution, the state will be new. Before entering the state machine, amend down replace order (38=G) will be validated before actual matching.

Order matching
--------------
1. From pending new queue, extract orders from it in FIFO. The order picked will be suspended to stop any incoming replace.
2. The order suspended will be checked against the order book. From the book like BUY order quantities available to match with SELL order (or vice versa), execute and put the remaining quantity of order to the order book based on side

Order book
----------
1. Order book has the following attributes: 1. Symbol (54); 2. Buy order queues and 3. Sell order queues
2. Buy/Sell order queues support spread queuing. For each price, it supports a queue of different order queuing up to be matched

Execution book
--------------
1. An execution consists of: 1. ExecID (FIX Tag 17), Active Order ID (FIX Tag 11), Passive Order ID (FIX Tag 37), Execution Report (Tag 35: 8), Quantity (FIX Tag 38), Price (Tag 44), Side for Active order (Tag 54), Symbol (Tag 55), execution time (FIX Tag 60)

Order Status Publishing
-----------------------
1. For order status changes, the engine will publish execution report (Tag 35: 8)

Spread Calculation
------------------
1. There are different spread values at different price ranges.
2. Ranges are:
<p>0.01 - 0.25 -> 0.001</p>
<p>0.25 - 0.50 -> 0.005</p>
<p>0.50 - 10.00 -> 0.010</p>
<p>10.00 - 20.00 -> 0.020</p>
<p>20.00 - 100.00 -> 0.050</p>
<p>100.00 - 200.00 -> 0.100</p>
<p>200.00 - 500.00 -> 0.200</p>
<p>500.00 - 1000.00 -> 0.500</p>
<p>1000.00 - 2000.00 -> 1.000</p>

Engine Start/Stop
-----------------
1. To start up the engine, LOGON (Tag 35:A) will be sent with last traded price (FIX tag 44) and symbol (FIX tag 55).
2. To stop the engine, LOGOUT (Tag 35: 5) will be sent to the engine.
3. Once engine is logged out, non-DFD/non-cancelled orders will be DFD and publish out.
4. The engine start/stop is being controlled by an internal clock to manage the start/stop time.
5. When the engine is in logged_on state, NOS/Replace/Cancel orders can be accepted. Otherwise these orders will be rejected.
