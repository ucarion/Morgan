package com.ulyssecarion.morgan.graph;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Element;

public class GraphDriver {
	/**
	 * An atom stores its element and the bonds its in.
	 * 
	 * @see PDBAtom which also stores an atom name
	 * @author Ulysse Carion
	 */
	public static class Atom {
		private Element element;
		private List<Bond> bonds;

		private int currentConnectivity;
		private int nextConnectivity;

		public Atom(Element element) {
			this.element = element;
			bonds = new ArrayList<>();
			currentConnectivity = -1;
			nextConnectivity = -1;
		}

		public Element getElement() {
			return element;
		}

		public List<Bond> getBonds() {
			return bonds;
		}

		@Override
		public String toString() {
			String s = element + " -> [ ";
			for (Bond b : bonds) {
				s += b.getOther(this).element + "-" + b.getOrder() + " ";
			}
			return s + "] (" + getHashValue() + ")";
		}

		public void initializeConnectivity() {
			nextConnectivity = getHashValue();
		}

		public void prepareNextConnectivity() {
			int nextVal = 0;

			for (Bond bond : bonds) {
				nextVal += bond.getOther(this).currentConnectivity;
			}

			nextConnectivity = nextVal + currentConnectivity;
		}

		public void useNextConnectivity() {
			currentConnectivity = nextConnectivity;
			nextConnectivity = -1;
		}

		public int getCurrentConnectivity() {
			return currentConnectivity;
		}

		public int getNextConnectivity() {
			return nextConnectivity;
		}

		public int getHashValue() {
			int atomicNumberScore = 10 * element.getAtomicNumber();
			int piCenterScore = isPiCenter() ? 1 : 0;

			return atomicNumberScore + piCenterScore;
		}

		public boolean isPiCenter() {
			return hasHighOrderBond() || isInInterchangeableBond();
		}

		private boolean hasHighOrderBond() {
			for (Bond bond : bonds) {
				if (bond.getOrder() > 1) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Returns true if all the following are true:
		 * <ol>
		 * <li>This atom is electronegative.</li>
		 * <li>This atom is single bonded to some other atom <i>A</li> and
		 * <i>A</i> is in turn double-bonded to some electronegative atom.
		 * </ol>
		 * 
		 * @return true if this atom is in some kind of bond whose bond order is
		 *         not really '1' but closer to '1.5' because it's shared with
		 *         another atom.
		 */
		private boolean isInInterchangeableBond() {
			if (!isElectronegative()) {
				return false;
			}

			for (Bond bond : bonds) {
				if (bond.getOrder() == 1) {
					Atom candidateCenter = bond.getOther(this);
					for (Bond centerBond : candidateCenter.bonds) {
						if (centerBond.getOrder() == 2) {
							Atom candidateAlternate = centerBond
									.getOther(candidateCenter);

							if (candidateAlternate.isElectronegative()) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}

		public boolean isElectronegative() {
			int valence = element.getValenceElectronCount();

			return valence >= 5 && valence <= 7;
		}
	}

	/**
	 * Like a regular Atom, but it also has an atom name.
	 * 
	 * @author Ulysse Carion
	 */
	public static class PDBAtom extends Atom {
		private String atomName;

		public PDBAtom(Element element, String atomName) {
			super(element);
			this.atomName = atomName;
		}

		public String getAtomName() {
			return atomName;
		}

		@Override
		public String toString() {
			String s = getElement() + " (" + atomName + ") -> [ ";
			for (Bond b : getBonds()) {
				s += b.getOther(this).element + "-" + b.getOrder() + " ";
			}
			return s + "] (" + getHashValue() + ")";
		}
	}

	/**
	 * Keeps track of two atoms and the number of electrons they covalently
	 * share. Remember to call {@link Bond#addSelfToAtoms()} after creating one
	 * of these.
	 * 
	 * @author Ulysse Carion
	 */
	public static class Bond {
		private Atom a;
		private Atom b;
		private int order;

		public Bond(Atom a, Atom b, int order) {
			this.a = a;
			this.b = b;
			this.order = order;
		}

		public Atom getOther(Atom exclude) {
			return exclude == a ? b : a;
		}

		public int getOrder() {
			return order;
		}

		public void addSelfToAtoms() {
			a.getBonds().add(this);
			b.getBonds().add(this);
		}
	}
}
