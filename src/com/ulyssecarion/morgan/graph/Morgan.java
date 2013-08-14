package com.ulyssecarion.morgan.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ulyssecarion.morgan.graph.GraphDriver.Atom;
import com.ulyssecarion.morgan.graph.GraphDriver.PDBAtom;

public class Morgan {
	public static void main(String[] args) {
		List<PDBAtom> pdbGraph = PDBGraphMaker.getGraph("TYR");
		List<Atom> cmxGraph = ChemAxonGraphMaker
				.getGraph("N[C@@H](Cc1ccc(O)cc1)C(O)=O");

		morganify(pdbGraph);
		morganify(cmxGraph);

		for (Atom atom : pdbGraph) {
			System.out.println(atom.getCurrentConnectivity() + "\t: " + atom);
		}

		System.out.println("---");

		for (Atom atom : cmxGraph) {
			System.out.println(atom.getCurrentConnectivity() + "\t: " + atom);
		}

		System.out.println("---");

		System.out.println("ChemAxon atom names might be:");
		for (Atom atom : cmxGraph) {
			System.out.println(atom);
			System.out.println(getAtomNamesOfCMXAtom(atom, pdbGraph));
			System.out.println();
		}
	}

	/**
	 * Runs the morgan algorithm on a graph of atoms. After running this method,
	 * all atoms will have a currentConnectivity value that can be used to
	 * identify them.
	 * <p>
	 * Note: the connectivity values will not be unique. Symmetric molecules or
	 * ones that have pi-centers in them will likely have multiple atoms with
	 * the same connectivity values. This ambiguity is intentional.
	 * 
	 * @param graph
	 *            a list of atoms that constitute a molecule
	 */
	public static void morganify(List<? extends Atom> graph) {
		for (Atom atom : graph) {
			atom.initializeConnectivity();
		}

		int currentN = 0;
		while (getNumUniqueValues(graph) > currentN) {
			currentN = getNumUniqueValues(graph);

			for (Atom atom : graph) {
				atom.useNextConnectivity();
			}

			for (Atom atom : graph) {
				atom.prepareNextConnectivity();
			}
		}
	}

	/**
	 * Gets potential atom names of a given Atom by looking at that atom's
	 * corresponding entries in the PDB. This assumes that
	 * {@link #morganify(List)} has already been run.
	 * 
	 * @param cmxAtom
	 *            the atom whose name you are looking for. This should be an
	 *            Atom returned from {@link ChemAxonGraphMaker#getGraph(String)}
	 *            .
	 * @param pdbGraph
	 *            the PDB graph that corresponds to cmxAtom's group. If cmxAtom
	 *            comes from an ALA read in by ChemAxonGroupMaker, then this
	 *            graph should be based on the PDB's version of ALA. This graph
	 *            should have been made by
	 *            {@link PDBGraphMaker#getGraph(String)}.
	 * @return a list of possible names for the passed atom.
	 */
	public static List<String> getAtomNamesOfCMXAtom(Atom cmxAtom,
			List<PDBAtom> pdbGraph) {
		List<String> names = new ArrayList<>();

		for (PDBAtom pdbAtom : pdbGraph) {
			if (cmxAtom.getCurrentConnectivity() == pdbAtom
					.getCurrentConnectivity()) {
				names.add(pdbAtom.getAtomName());
			}
		}

		return names;
	}

	private static int getNumUniqueValues(List<? extends Atom> atoms) {
		Set<Integer> s = new HashSet<Integer>();

		for (Atom atom : atoms) {
			s.add(atom.getNextConnectivity());
		}

		return s.size();
	}
}
