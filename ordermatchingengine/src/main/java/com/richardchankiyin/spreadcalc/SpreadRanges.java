package com.richardchankiyin.spreadcalc;


public class SpreadRanges {
	SpreadRangeNode[] srn = null;
	SpreadRangeNode firstNode = null;
	SpreadRangeNode lastNode = null;
	private class SpreadRangeNode {
		SpreadRange sr = null;
		SpreadRangeNode prev = null;
		SpreadRangeNode next = null;
		SpreadRangeNode(SpreadRange sr) {
			this.sr = sr;
		}
		void setPrev(SpreadRangeNode prev) {
			this.prev = prev;
		}
		void setNext(SpreadRangeNode next) {
			this.next = next;
		}
		SpreadRange getSpreadRange() {
			return this.sr;
		}
		SpreadRangeNode getPrev() { 
			return this.prev;
		}
		SpreadRangeNode getNext() {
			return this.next;
		}
	}
	
	
	private SpreadRanges() {
		// HKEx spread
		srn = new SpreadRangeNode[11];
		srn[0] = new SpreadRangeNode(new SpreadRange(0.01,0.25,0.001));
		srn[1] = new SpreadRangeNode(new SpreadRange(0.25,0.50,0.005));
		srn[2] = new SpreadRangeNode(new SpreadRange(0.50,10.00,0.01));
		srn[3] = new SpreadRangeNode(new SpreadRange(10.00,20.00,0.02));
		srn[4] = new SpreadRangeNode(new SpreadRange(20.00,100.00,0.05));
		srn[5] = new SpreadRangeNode(new SpreadRange(100.00,200.00,0.1));
		srn[6] = new SpreadRangeNode(new SpreadRange(200.00,500.00,0.2));
		srn[7] = new SpreadRangeNode(new SpreadRange(500.00,1000.00,0.5));
		srn[8] = new SpreadRangeNode(new SpreadRange(1000.00,2000.00,1));
		srn[9] = new SpreadRangeNode(new SpreadRange(2000.00,5000.00,2));
		srn[10] = new SpreadRangeNode(new SpreadRange(5000.00,10000.00,5));
		
		firstNode = srn[0];
		lastNode = srn[10];
		
		for (int i = 0; i < srn.length; i++) {
			int before = i - 1;
			int after = i + 1;
			
			if (before >= 0)
				srn[i].setPrev(srn[before]);
			if (after <= srn.length - 1)
				srn[i].setNext(srn[after]);
		}
		
	}
	
	private static volatile SpreadRanges instance = null;
	
	public static SpreadRanges getInstance() {
		if (instance == null) {
			synchronized(SpreadRanges.class) {
				if (instance == null) {
					instance = new SpreadRanges();
					return instance;
				} else {
					return instance;
				}
			}
		} else {
			return instance;
		}
	}

	public boolean isValidPrice(double price, boolean isGoUp) {
		return getSpreadRangeNode(price, isGoUp) != null;
	}
	
	private SpreadRangeNode getSpreadRangeNode(double price, boolean isGoUp) {
		SpreadRangeNode startingNode = isGoUp ? firstNode : lastNode;
		boolean result = false;
		boolean willContinue = true;
		do {
			SpreadRange sr = startingNode.getSpreadRange();
			result = sr.isInRange(price, isGoUp);
			if (result) {
				willContinue = false;
			} else {
				if (isGoUp) {
					if (price < sr.getEndWith()) {
						willContinue = false;
					} else {
						startingNode = startingNode.getNext();
						if (startingNode == null) {
							willContinue = false;
						}
					}
				} else {
					if (price > sr.getStartFrom()) {
						willContinue = false;
					} else {
						startingNode = startingNode.getPrev();
						if (startingNode == null) {
							willContinue = false;
						}
					}
				}
			}
		} while (willContinue);
		return result ? startingNode: null;
	}
}
