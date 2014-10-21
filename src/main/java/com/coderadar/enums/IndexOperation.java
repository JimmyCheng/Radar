package com.coderadar.enums;

public enum IndexOperation {
	Update, Delete, NoAction;

	/**
	 * Get the operation by the SvnState.
	 * 
	 * @param state
	 * @return
	 */
	public static IndexOperation getOperationBySvnState(SvnState state) {
		switch (state) {
		case A:
		case M:
			return Update;
		case D:
			return Delete;
		default:
			return NoAction;
		}
	}
}
