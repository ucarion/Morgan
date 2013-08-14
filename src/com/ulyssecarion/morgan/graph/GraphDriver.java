package com.ulyssecarion.morgan.graph;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.structure.Element;

public class GraphDriver {
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
