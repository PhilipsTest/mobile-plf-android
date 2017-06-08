package com.philips.platform.appinfra.appupdate;

/**
 * This class is used to compare versions and return the values.
 */
class AppUpdateVersion {

	/**
	 * This method compares two verions .
	 * @param appVer
	 * @param cloudVer
	 * @return
	 */
	private static int compareVersion(String appVer, String cloudVer) {
		if (appVer != null && !appVer.isEmpty() && cloudVer != null && !cloudVer.isEmpty()) {
			String[] arr1 = appVer.split("\\.");
			String[] arr2 = cloudVer.split("\\.");

			int i = 0;
			while (i < arr1.length || i < arr2.length) {
				if (i < arr1.length && i < arr2.length) {
					if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
						return -1;
					} else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
						return 1;
					}
				} else if (i < arr1.length) {
					if (Integer.parseInt(arr1[i]) != 0) {
						return 1;
					}
				} else if (i < arr2.length) {
					if (Integer.parseInt(arr2[i]) != 0) {
						return -1;
					}
				}

				i++;
			}
			return 0;
		}
		return -1;
	}

	/**
	 * This method returns true if version < cloudver
	 * @param version version
	 * @param cloudver cloud version
	 * @return
	 */
	static boolean isAppVerionLessthanCloud(String version, String cloudver) {
		if (compareVersion(splitVersion(version), splitVersion(cloudver)) == -1) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if both versions are same.
	 * @param version
	 * @param cloudver
	 * @return
	 */
	static boolean isBothVersionSame(String version, String cloudver) {
		if (compareVersion(splitVersion(version), splitVersion(cloudver)) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method returns true if version <= cloudversion.
	 * @param version
	 * @param cloudver
	 * @return
	 */
	static boolean isAppVersionLessthanEqualsto(String version, String cloudver) {
		if (compareVersion(splitVersion(version), splitVersion(cloudver)) == -1 ||
				compareVersion(splitVersion(version), splitVersion(cloudver)) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method split the version and fetches the first 3 numbers . For example if version is
	 * 1.2.3-SNAPSHOT , this method return 1.2.3.
	 * @param version
	 * @return
	 */
	private static String splitVersion(String version) {
		if (version != null) {
			if (!version.matches("[0-9]+\\.[0-9]+\\.[0-9]+([_(-].*)?")) { // application format.
				throw new IllegalArgumentException("Invalid version format-AppUpdate");
			} else {
				String arr[] = version.split("-|_|\\(");  //splitting based on verion format.
				return arr[0].trim();
			}
		}
		return null;
	}

}
