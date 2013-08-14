package com.ulyssecarion.morgan.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ulyssecarion.morgan.graph.GraphDriver.Atom;

public class Morgan {
	public static void main(String[] args) {
		List<Atom> pdbGraph = ChemAxonGraphMaker.getGraph();
		
		for (Atom atom : pdbGraph) {
			System.out.println(atom);
		}
		
		for (Atom atom : pdbGraph) {
			atom.initializeConnectivity();
		}
		
		int currentN = 0;
		while (getNumUniqueValues(pdbGraph) > currentN) {
			currentN = getNumUniqueValues(pdbGraph);
			
			System.out.println(currentN);
			
			for (Atom atom : pdbGraph) {
				atom.useNextConnectivity();
			}
			
			for (Atom atom : pdbGraph) {
				atom.prepareNextConnectivity();
			}
		}
		
		for (Atom atom : pdbGraph) {
			System.out.println(atom + " : " + atom.getCurrentConnectivity());
		}
	}
	
	private static int getNumUniqueValues(List<Atom> atoms) {
		Set<Integer> s = new HashSet<Integer>();
		
		for (Atom atom : atoms) {
			s.add(atom.getNextConnectivity());
		}
		
		return s.size();
	}
}
