package com.mba.analyst_sr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class will control the course of
 * market basket analysis. 
 * 
 * @author Sendi Setiawan
 *
 */
public class MarketBasketAnalysis {
	
	//Constructor
	public MarketBasketAnalysis() {
		this.tree = new NBTree();
		this.transactionData = new TransactionData();
	}
	
	/**
	 * This method will process data file
	 * from reading, cleaning, converting,
	 * and creating initial item set
	 */
	public void processFile() {
		transactionData.setFile(file);
		transactionData.readFile();
		transactionData.countAllItem();
		transactionData.makeId();
		transactionData.createItemSet(threshold);
		transactionData.rewind();
	}
	
	/**
	 * This method will create initial tree
	 * that contains subset from initial item set
	 */
	public void makeInitialTree() {
		ArrayList<Integer> itemSet = transactionData.getItemSet();
		Collections.sort(itemSet);
		ArrayList<ArrayList<Integer>> subset = Subset.getSubset(itemSet, 2);
		if(tree.isEmptyTree() == false) {
			tree.resetTree();
		}
		for(ArrayList<Integer> subs : subset) {
				Integer[] sub = new Integer[subs.size()];
				sub = subs.toArray(sub);
				tree.insert(sub);
		}
	}
	
	/**
	 * This method will calculate all subset supports in the tree
	 * 
	 */
	public void countAllItemsSupportOld() {
		ArrayList<ArrayList<Integer>> subset = new ArrayList<>();
		ArrayList<Integer> transaction = transactionData.getNextTransaction();
		ArrayList<Integer> itemSet = transactionData.getItemSet();
		
		while(transaction != null) {
			subset = Subset.getSubset(transaction, 2);
			for(ArrayList<Integer> subs : subset) {
				
				//check if subset has all item that frequent
				if(itemSet.containsAll(subs)) {
					Integer[] sub = new Integer[subs.size()];
					sub = subs.toArray(sub);
					tree.addSupport(sub);
				}
			}
			transaction = transactionData.getNextTransaction();
			subset.clear();
		}	
	}

	/**
	 * This method will calculate all subset supports in the tree
	 * 
	 */
	public void countAllItemsSupport() {
		ArrayList<ArrayList<Integer>> subset = new ArrayList<>();
		ArrayList<Integer> transaction = transactionData.getNextTransaction();
		ArrayList<Integer> itemSet = transactionData.getItemSet();
		
		while(transaction != null) {
			transaction.retainAll(itemSet);
			subset = Subset.getSubset(transaction, 2);
			for(ArrayList<Integer> subs : subset) {
				
					Integer[] sub = new Integer[subs.size()];
					sub = subs.toArray(sub);
					tree.addSupport(sub);
			}
			transaction = transactionData.getNextTransaction();
			subset.clear();
		}	
		transactionData.rewind();
	}
	
	/**
	 * Make final Analysis result for all rules
	 * 
	 * @return all final rules
	 */
	public ArrayList<Rules> getAnalysisResult() {
		ArrayList<Rules> result = tree.getAllRules(threshold, transactionData.getNumberOfTransaction());
		for(Rules rule : result) {
			rule.calculateConfidence();
			rule.calculateLift();
		}
		return result;
	}

	//Getter and Setter
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	private File file;
	private NBTree tree;
	private TransactionData transactionData;
	private int threshold;
}
