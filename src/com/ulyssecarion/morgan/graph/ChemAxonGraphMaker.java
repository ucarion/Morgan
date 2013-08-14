package com.ulyssecarion.morgan.graph;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Element;

import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.license.LicenseManager;
import chemaxon.license.LicenseProcessingException;
import chemaxon.struc.MolAtom;
import chemaxon.struc.MolBond;
import chemaxon.struc.Molecule;

import com.ulyssecarion.morgan.graph.GraphDriver.Atom;
import com.ulyssecarion.morgan.graph.GraphDriver.Bond;

public class ChemAxonGraphMaker {
	public static List<Atom> getGraph() {
		try {
			LicenseManager
					.setLicenseFile("/Users/ulysse/Documents/workspace/pdbinabox/licences/chemaxon/license.cxl");
		} catch (LicenseProcessingException e) {
			e.printStackTrace();
		}

		String input = "C[C@H](N)C(O)=O";
		Molecule m = null;
		try {
			m = MolImporter.importMol(input, "smiles");
		} catch (MolFormatException e) {
			e.printStackTrace();
		}

		List<Atom> atoms = new ArrayList<>();
		List<MolAtom> molAtoms = new ArrayList<>();
		
		for (MolAtom atom : m.getAtomArray()) {
			Element element = getElement(atom);
			
			if (element.isHeavyAtom()) {
				atoms.add(new Atom(element));
				molAtoms.add(atom);
			}
		}
		
		for (MolBond bond : m.getBondArray()) {
			MolAtom molAtom1 = bond.getAtom1();
			MolAtom molAtom2 = bond.getAtom2();

			if (getElement(molAtom1).isHeavyAtom()
					&& getElement(molAtom2).isHeavyAtom()) {
				Atom atom1 = atoms.get(molAtoms.indexOf(molAtom1));
				Atom atom2 = atoms.get(molAtoms.indexOf(molAtom2));
				
				new Bond(atom1, atom2, bond.getType()).addSelfToAtoms();
			}
		}
		
		return atoms;
	}
	
	public static Element getElement(MolAtom atom) {
		return Element.valueOfIgnoreCase(atom.getSymbol());
	}
}
