package models;

public class DataTypesConverter {

	public static boolean[] byteToBoolArr(byte j, int size) {
		boolean boolArr[] = new boolean[8];
		for (int i = 0; i < 8; i++)
			boolArr[i] = (j & (byte) (128 / Math.pow(2, i))) != 0;
		for (int i = 0; i < boolArr.length / 2; i++) {
			boolean pom = boolArr[i];
			boolArr[i] = boolArr[boolArr.length - i - 1];
			boolArr[boolArr.length - i - 1] = pom;
		}
		return getSliceOfArray(boolArr, size);
	}

	public static boolean[] getSliceOfArray(boolean[] arr, int end) {
		int start = 0;
		// Get the slice of the Array
		boolean[] slice = new boolean[end - start];

		// Copy elements of arr to slice
		for (int i = 0; i < slice.length; i++) {
			slice[i] = arr[start + i];
		}

		// return the slice
		return slice;
	}

	public static byte booleanArrayAsbyte(boolean[] boolArray) {
		int res = 0;
		for (int i = boolArray.length - 1; i >= 0; i--) {
			if (boolArray[i]) {
				res += (int) Math.pow(2, i);
			}
		}
		return (byte) res;
	}
}
