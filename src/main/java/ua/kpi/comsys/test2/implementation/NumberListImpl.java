/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import ua.kpi.comsys.test2.NumberList;

/**
 * Реалізація списку для представлення числа у вісімковій системі числення.
 * Використовується кільцевий двонаправлений зв'язний список.
 *
 * @author Семенюк Катерина Василівна
 * Група: ІС-33 
 * Варіант: 17
 */
public class NumberListImpl implements NumberList {

    private static final int BASE = 8; // вісімкова система
    private static final int ADDITIONAL_BASE = 10; // десяткова система для changeScale

    private Node head; // голова списку
    private int size; // розмір списку

    // Клас для вузла двонаправленого списку
    private static class Node {
        Byte data;
        Node next;
        Node prev;

        Node(Byte data) {
            this.data = data;
        }
    }

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                convertFromDecimal(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Помилка читання файлу", e);
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        convertFromDecimal(value);
    }

    // Конвертує десяткове число в вісімкове і додає в список
    private void convertFromDecimal(String decimalValue) {
        if (decimalValue == null || decimalValue.isEmpty() || decimalValue.equals("0")) {
            add((byte) 0);
            return;
        }

        // Переводимо з 10-кової в 8-кову систему
        BigInteger decimal = new BigInteger(decimalValue);
        String octalString = decimal.toString(BASE);

        // Додаємо кожну цифру в список
        for (int i = 0; i < octalString.length(); i++) {
            char c = octalString.charAt(i);
            byte digit = (byte) Character.digit(c, BASE);
            add(digit);
        }
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException("Помилка запису у файл", e);
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 4117;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        // Переводимо з вісімкової (поточна) в десяткову (додаткова система)
        String decimalValue = toDecimalString();

        // Створюємо новий список для десяткової системи
        NumberListImpl result = new NumberListImpl();

        // Додаємо кожну десяткову цифру
        for (int i = 0; i < decimalValue.length(); i++) {
            byte digit = (byte) Character.digit(decimalValue.charAt(i), ADDITIONAL_BASE);
            result.add(digit);
        }

        return result;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        // Додаткова операція: ціле ділення (С7 = 3)
        String dividend = this.toDecimalString();
        String divisor = ((NumberListImpl) arg).toDecimalString();

        BigInteger a = new BigInteger(dividend);
        BigInteger b = new BigInteger(divisor);

        if (b.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Ділення на нуль");
        }

        BigInteger result = a.divide(b);
        return new NumberListImpl(result.toString());
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) {
            return "0";
        }

        // Переводимо з вісімкової в десяткову
        String octalString = toString();
        BigInteger octalValue = new BigInteger(octalString, BASE);
        return octalValue.toString(10);
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            sb.append(current.data);
            current = current.next;
        } while (current != head);

        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberListImpl)) return false;

        NumberListImpl other = (NumberListImpl) o;

        // Порівнюємо за десятковим значенням
        return this.toDecimalString().equals(other.toDecimalString());
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }

        if (isEmpty()) {
            return false;
        }

        Node current = head;
        do {
            if (current.data.equals(o)) {
                return true;
            }
            current = current.next;
        } while (current != head);

        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new NumberListIterator();
    }

    // Ітератор для проходження по списку
    private class NumberListIterator implements Iterator<Byte> {
        private Node current = head;
        private int count = 0;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Byte data = current.data;
            current = current.next;
            count++;
            return data;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }
        return array;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        // Не потрібно реалізовувати за завданням
        return null;
    }


    @Override
    public boolean add(Byte e) {
        if (e == null) {
            throw new NullPointerException("Null елементи не дозволені");
        }

        if (e < 0 || e >= BASE) {
            throw new IllegalArgumentException("Цифра має бути в діапазоні [0, " + (BASE - 1) + "]");
        }

        Node newNode = new Node(e);

        if (isEmpty()) {
            // Якщо список порожній, створюємо перший елемент
            head = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
        } else {
            // Додаємо в кінець (перед head)
            Node tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }

        size++;
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte) || isEmpty()) {
            return false;
        }

        Node current = head;
        do {
            if (current.data.equals(o)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        } while (current != head);

        return false;
    }

    // Видаляє вузол зі списку
    private void removeNode(Node node) {
        if (size == 1) {
            head = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            if (node == head) {
                head = node.next;
            }
        }
        size--;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Індекс: " + index + ", Розмір: " + size);
        }

        boolean modified = false;
        for (Byte e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;

        if (isEmpty()) {
            return false;
        }

        Node current = head;
        Node start = head;
        do {
            Node next = current.next;
            if (c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        } while (current != start && !isEmpty());

        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;

        if (isEmpty()) {
            return false;
        }

        Node current = head;
        Node start = head;
        do {
            Node next = current.next;
            if (!c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        } while (current != start && !isEmpty());

        return modified;
    }


    @Override
    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Індекс: " + index + ", Розмір: " + size);
        }

        return getNode(index).data;
    }

    // Знаходить вузол за індексом
    private Node getNode(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }


    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Індекс: " + index + ", Розмір: " + size);
        }

        if (element == null) {
            throw new NullPointerException("Null елементи не дозволені");
        }

        if (element < 0 || element >= BASE) {
            throw new IllegalArgumentException("Цифра має бути в діапазоні [0, " + (BASE - 1) + "]");
        }

        Node node = getNode(index);
        Byte oldValue = node.data;
        node.data = element;
        return oldValue;
    }


    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Індекс: " + index + ", Розмір: " + size);
        }

        if (element == null) {
            throw new NullPointerException("Null елементи не дозволені");
        }

        if (element < 0 || element >= BASE) {
            throw new IllegalArgumentException("Цифра має бути в діапазоні [0, " + (BASE - 1) + "]");
        }

        if (index == size) {
            add(element);
            return;
        }

        Node newNode = new Node(element);

        if (index == 0) {
            if (isEmpty()) {
                head = newNode;
                newNode.next = newNode;
                newNode.prev = newNode;
            } else {
                Node tail = head.prev;
                newNode.next = head;
                newNode.prev = tail;
                tail.next = newNode;
                head.prev = newNode;
                head = newNode;
            }
        } else {
            Node current = getNode(index);
            newNode.next = current;
            newNode.prev = current.prev;
            current.prev.next = newNode;
            current.prev = newNode;
        }

        size++;
    }


    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Індекс: " + index + ", Розмір: " + size);
        }

        Node node = getNode(index);
        Byte data = node.data;
        removeNode(node);
        return data;
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte) || isEmpty()) {
            return -1;
        }

        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.data.equals(o)) {
                return i;
            }
            current = current.next;
        }

        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte) || isEmpty()) {
            return -1;
        }

        Node current = head.prev;
        for (int i = size - 1; i >= 0; i--) {
            if (current.data.equals(o)) {
                return i;
            }
            current = current.prev;
        }

        return -1;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        return new NumberListListIterator(0);
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Індекс: " + index + ", Розмір: " + size);
        }
        return new NumberListListIterator(index);
    }

    // ListIterator для двонаправленого проходження
    private class NumberListListIterator implements ListIterator<Byte> {
        private Node current;
        private Node lastReturned;
        private int index;

        NumberListListIterator(int index) {
            this.index = index;
            if (index == size) {
                current = null;
            } else if (size > 0) {
                current = getNode(index);
            }
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturned = current;
            Byte data = current.data;
            current = current.next;
            index++;
            return data;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public Byte previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            if (current == null) {
                current = head.prev;
            } else {
                current = current.prev;
            }
            lastReturned = current;
            index--;
            return current.data;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            Node next = lastReturned.next;
            removeNode(lastReturned);

            if (current == lastReturned) {
                current = next;
            } else {
                index--;
            }

            lastReturned = null;
        }

        @Override
        public void set(Byte e) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            if (e == null) {
                throw new NullPointerException("Null елементи не дозволені");
            }
            if (e < 0 || e >= BASE) {
                throw new IllegalArgumentException("Цифра має бути в діапазоні [0, " + (BASE - 1) + "]");
            }
            lastReturned.data = e;
        }

        @Override
        public void add(Byte e) {
            if (e == null) {
                throw new NullPointerException("Null елементи не дозволені");
            }
            if (e < 0 || e >= BASE) {
                throw new IllegalArgumentException("Цифра має бути в діапазоні [0, " + (BASE - 1) + "]");
            }

            NumberListImpl.this.add(index, e);
            index++;
            lastReturned = null;
        }
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        NumberListImpl subList = new NumberListImpl();
        for (int i = fromIndex; i < toIndex; i++) {
            subList.add(get(i));
        }
        return subList;
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }

        if (index1 == index2) {
            return true;
        }

        Node node1 = getNode(index1);
        Node node2 = getNode(index2);

        // Міняємо дані місцями
        Byte temp = node1.data;
        node1.data = node2.data;
        node2.data = temp;

        return true;
    }


    @Override
    public void sortAscending() {
        if (size <= 1) {
            return;
        }

        // Сортування бульбашкою по зростанню
        for (int i = 0; i < size - 1; i++) {
            Node current = head;
            for (int j = 0; j < size - i - 1; j++) {
                if (current.data > current.next.data) {
                    Byte temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                }
                current = current.next;
            }
        }
    }


    @Override
    public void sortDescending() {
        if (size <= 1) {
            return;
        }

        // Сортування бульбашкою по спаданню
        for (int i = 0; i < size - 1; i++) {
            Node current = head;
            for (int j = 0; j < size - i - 1; j++) {
                if (current.data < current.next.data) {
                    Byte temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                }
                current = current.next;
            }
        }
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) {
            return;
        }

        // Циклічний зсув вліво - просто переміщуємо голову
        head = head.next;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) {
            return;
        }

        // Циклічний зсув вправо
        head = head.prev;
    }
}
