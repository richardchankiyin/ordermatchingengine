# ordermatchingengine
This is a repository about the implementation of order matching engine written in java

Requirements
=============
Order validation
-----------------
1. Only orders with buy/sell limit/market orders will be accepted. An order needs to have ClOrdID (FIX Tag 11), MsgType (FIX Tag 35), Side (FIX Tag54), Symbol (FIX Tag 55), OrdType (FIX Tag 40), Price (FIX Tag 44 only required in limit) andOrderQty (FIX Tag 38).

2. Order amendment is being supported with limited manner. Only amend down order (Tag 38) with value less than original one will be accepted. Otherwise orders will be rejected. For others like amend up/change price orders, cancel and new orders done in two steps required

3. Order cancel can be done anytime except order is pending execution, or fille

4. 



