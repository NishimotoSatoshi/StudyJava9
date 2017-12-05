module opst.exp4.bar {
	provides jp.co.opst.java9.mod.exp4.NameService
		with jp.co.opst.java9.mod.exp4.bar.NameServiceBar;

	requires opst.exp4;
}
