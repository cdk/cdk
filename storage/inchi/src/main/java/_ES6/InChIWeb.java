package _ES6;

public class InChIWeb {

	/**
	 * JavaScript only
	 * 
	 * Load inchi-web.wasm asynchronously.
	 * 
	 */
    public static void importWASM() {
		try {
			/**
			 * Import inchi-web-SwingJS.js
			 * 
			 * @j2sNative 
			 * 
			 * var j2sPath = J2S._applets.master._j2sFullPath; 
			 * J2S.inchiPath = J2S._applets.master._j2sFullPath + "/_ES6"; 
			 * $.getScript(J2S.inchiPath +   "/inchi-web-SwingJS.js");
			 */
			{
			}
		} catch (Throwable t) {
			//
		}
	}

	public static void initAndRun(Runnable r) {
		/**
		 * @j2sNative
		 *    
		 *    if (!J2S) {
		 *      alert("J2S has not been installed");
		 *      System.exit(0);
		 *    }
		 *   var t = [];
		 *   t[0] = setInterval(
		 *      function(){
		 *       if (J2S.inchiWasmLoaded && J2S.inchiWasmLoaded()) {
		 *        clearInterval(t[0]);
		 *        System.out.println("InChI WASM initialized successfully");
		 *        r.run$();
		 *       }
		 *      }, 50);
		 */
	}

	
}