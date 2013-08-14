package com.ulyssecarion.morgan.graph;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Element;
import org.biojava.bio.structure.io.mmcif.ChemCompGroupFactory;
import org.biojava.bio.structure.io.mmcif.model.ChemComp;
import org.biojava.bio.structure.io.mmcif.model.ChemCompAtom;
import org.biojava.bio.structure.io.mmcif.model.ChemCompBond;

import com.ulyssecarion.morgan.graph.GraphDriver.Atom;
import com.ulyssecarion.morgan.graph.GraphDriver.Bond;
import com.ulyssecarion.morgan.graph.GraphDriver.PDBAtom;

public class PDBGraphMaker {
	/**
	 * Gets a list of atoms that are part of a ChemComp, with bonds already
	 * formed for you.
	 * 
	 * @param name
	 *            the name of the ChemComp, like "ALA" or "HEM".
	 * @return a list of PDBAtoms that make up a ChemComp
	 */
	public static List<PDBAtom> getGraph(String name) {
		ChemComp cc = ChemCompGroupFactory.getChemComp(name);

		List<PDBAtom> atoms = new ArrayList<>();
		List<String> atomNames = new ArrayList<>();

		for (ChemCompAtom atom : cc.getAtoms()) {
			Element element = getElement(atom);

			if (element.isHeavyAtom()) {
				atoms.add(new PDBAtom(element, atom.getAtom_id()));
				atomNames.add(atom.getAtom_id());
			}
		}

		for (ChemCompBond bond : cc.getBonds()) {
			String atomName1 = bond.getAtom_id_1();
			String atomName2 = bond.getAtom_id_2();

			int index1 = atomNames.indexOf(atomName1);
			int index2 = atomNames.indexOf(atomName2);

			if (index1 != -1 && index2 != -1) {
				Atom atom1 = atoms.get(index1);
				Atom atom2 = atoms.get(index2);

				new Bond(atom1, atom2, bond.getNumericalBondOrder())
						.addSelfToAtoms();
			}
		}

		return atoms;
	}

	private static Element getElement(ChemCompAtom atom) {
		return Element.valueOfIgnoreCase(atom.getType_symbol());
	}
}
