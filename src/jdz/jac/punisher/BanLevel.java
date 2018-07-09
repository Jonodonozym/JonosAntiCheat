
package jdz.jac.punisher;

public enum BanLevel {
	UNIQUE, LIGHT, MEDIUM, HEAVY, PERMANENT;

	public boolean isTemp() {
		return this == LIGHT || this == MEDIUM || this == HEAVY;
	}

	public static BanLevel[] temp() {
		return new BanLevel[] { LIGHT, MEDIUM, HEAVY };
	}
}
