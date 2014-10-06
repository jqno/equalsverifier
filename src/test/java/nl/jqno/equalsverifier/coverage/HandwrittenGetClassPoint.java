package nl.jqno.equalsverifier.coverage;

import nl.jqno.equalsverifier.testhelpers.types.Color;

public final class HandwrittenGetClassPoint {
	private final int x;
	private final int y;
	private final Color color;
	
	public HandwrittenGetClassPoint(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		HandwrittenGetClassPoint other = (HandwrittenGetClassPoint)obj;
		return x == other.x && y == other.y && (color == null ? other.color == null : color.equals(other.color));
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + x;
		result = 31 * result + y;
		result = 31 * result + (color == null ? 0 : color.hashCode());
		return result;
	}
}
