package com.hackerhop.leaderboards.sql.utils;

import java.util.Arrays;

/**
 * Basically an array, but with additional functions and methods to improve productivity and efficiency.
 */
public class ArrayPlus<T> {
	private Object[] array;

	// Store the indexes of the current slice.
	private int lowerBound;
	private int upperBound;

	/**
	 * Creates a new array with the given size.
	 *
	 * @param size The size of the underlying array to be created.
	 */
	public ArrayPlus(int size) {
		array = new Object[size];
		lowerBound = 0;
		upperBound = size;
	}

	/**
	 * Creates a new array with the given underlying array.
	 *
	 * @param array the array to initialize the ArrayPlus with.
	 */
	public ArrayPlus(T[] array) {
		this(array, 0, array.length);
	}

	/**
	 * Creates a new array with the given underlying array and slice.
	 *
	 * @param array      the array to initialize the ArrayPlus with.
	 * @param lowerBound the lower bound of the slice, inclusive.
	 * @param upperBound the upper bound of the slice, exclusive.
	 */
	public ArrayPlus(T[] array, int lowerBound, int upperBound) {
		if (upperBound < lowerBound) {
			throw new NegativeArraySizeException();
		} else if (lowerBound > array.length || upperBound > array.length) {
			throw new ArrayIndexOutOfBoundsException();
		}

		this.array = array;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * Sets the value of the element at the given position.
	 *
	 * @param position the position of the element to modify.
	 * @param value    the new value to give the element.
	 */
	public void set(int position, T value) {
		if (position >= 0 && position <= (upperBound - lowerBound)) {
			array[lowerBound + position] = value;
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the element at the given position.
	 *
	 * @param position the position lo look up.
	 * @return the element at the given position.
	 */
	@SuppressWarnings("unchecked")
	public T get(int position) {
		if (position >= 0 && position <= (upperBound - lowerBound)) {
			return (T) array[lowerBound + position];
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * Creates a slice of the ArrayPlus.
	 *
	 * @param lowerBound the lower bound of the slice, inclusive.
	 * @param upperBound the upper bound of the slice, exclusive.
	 * @return the sliced ArrayPlus.
	 */
	@SuppressWarnings("unchecked")
	public ArrayPlus<T> slice(int lowerBound, int upperBound) {
		if (upperBound < lowerBound) {
			throw new NegativeArraySizeException();
		} else if (lowerBound + this.lowerBound > this.upperBound || upperBound > this.upperBound) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return new ArrayPlus<>((T[]) array, this.lowerBound + lowerBound, this.lowerBound + upperBound);
	}

	/**
	 * Takes the slice of the ArrayPlus, from the provided lower bound to the size of the array.
	 *
	 * @param lowerBound the lower bound of the slice, inclusive.
	 * @return the sliced ArrayPlus.
	 */
	public ArrayPlus<T> slice(int lowerBound) {
		return slice(lowerBound, upperBound);
	}

	/**
	 * Returns the size of the slice.
	 *
	 * @return the size of the slice.
	 */
	public int size() {
		return upperBound - lowerBound;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ArrayPlus)) {
			return false;
		}

		ArrayPlus other = (ArrayPlus) obj;

		return Arrays.equals(array, other.array) &&
				lowerBound == other.lowerBound &&
				upperBound == other.upperBound;
	}
}
