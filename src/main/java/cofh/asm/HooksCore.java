package cofh.asm;

import java.util.Comparator;
import java.util.List;


public class HooksCore {

	public static void getObjectClass(Object aObject) {

		System.out.println("INJECT OBJ: " + aObject.getClass().getName());
	}


	public static <T extends Object> void sort(List<T> list, Comparator<T> cmp) {

		quickSortList(list, 0, list.size(), 0, cmp);
	}

	private static <T> void quickSortList(List<T> input, int left, int right, int d, Comparator<T> cmp) {
		if (left >= right) return;
		int j = right, i = left;
		int size = right - left;
		T pivotPoint = input.get(((right >>> 1) + (left >>> 1))), t;
		do {
			if (size <= 7) {
				pivotPoint = input.get(left);
				do {
					do {
						++left;
						if (cmp.compare(input.get(left), pivotPoint) < 0) {
							pivotPoint = input.get(left);
							do { // this section can be improved.
								input.set(left--, t = input.get(left));
							} while (left > i && cmp.compare(pivotPoint, t) < 0);
							input.set(left, pivotPoint);
						}
					} while (left < right);
					++i;
					left = i;
					pivotPoint = input.get(left);
				} while (i < right);
				return;
			}
			while (left < right) {
				while (cmp.compare(input.get(right), pivotPoint) > 0)
					--right;
				while (cmp.compare(input.get(left), pivotPoint) < 0)
					++left;
				if (left < right) {
					t = input.get(left);
					input.set(left, input.get(right));
					input.set(right, t);
					++left;
					--right;
				}
			}
			if (right > 0) {
				if (left == right) {
					if (cmp.compare(input.get(left), pivotPoint) < 0) ++left;
					else if (cmp.compare(input.get(right), pivotPoint) > 0) --right;
				}
				if (i < right) {
					quickSortList(input, i, right, d + 1, cmp);
				}
			}
			left |= (left == 0) & (right == 0) ? 1 : 0;
			if (j <= left) return;
			i = left;
			right = j;
			pivotPoint = input.get(((right >>> 1) + (left >>> 1)));
			size = right - left;
			++d;
		} while (true);
	}

}
