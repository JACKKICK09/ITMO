package Mannagers;

import Errors.EmptyFile;
import Errors.NonExistingElementException;
import NetWork.Outputter;
import Objects.Person;
import Objects.Product;
import Objects.ProductModel;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionRealizer implements Iterable<Product> {
    private static Vector<Product> CollectionRealizer = new Vector<>();
    private static int idCounter = 0;
    public CompareProduct comparator;
    private Date InitializerDate;

    public static CSVPars csvPars;

    public static Vector<Product> collection;

    public CollectionRealizer() {
        comparator = new CompareProduct();
        collection = new Vector<>();
        this.InitializerDate = new Date();

//        for(Product p : new CollectionRealizer()) {
//
//        }
    }

    @Override
    public Iterator<Product> iterator() {
        return new CollectionIterator();
    }

    private class CollectionIterator implements Iterator<Product> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < collection.size();
        }

        @Override
        public Product next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return collection.get(currentIndex++);
        }
    }

    public Vector<Product> add(ProductModel element){
        idCounter++;
        Product product = new Product(idCounter,
                element.getName(),
                element.getCoordinates(),
                LocalDateTime.now(),
                element.getPrice(),
                element.getManufactureCost(),
                element.getUnitOfMeasure(),
                element.getOwner()
        );
        collection.add(product);
        return sortByCoords(collection);
    }

    public Vector<Product> show(){
        return sortByCoords(collection);
    }

    public String info(){
        return "Тип коллекции: " + collection.getClass() + "\n"
                + "Дата создания: " + InitializerDate + "\n"
                + "Количество элементов: " + collection.size();
    }

    public Vector<Product> clear(){
        collection.clear();
        return sortByCoords(collection);
    }

    public Vector<Product> removeById(int id) throws NonExistingElementException {
        if (!collection.removeIf(product -> product.getId() == id)){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }
        return sortByCoords(collection);
    }

    public Vector<Product> removeLower(int startId) throws NonExistingElementException {
        int sizeColl = collection.size();
        if (sizeColl < startId){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }

        collection.removeIf(product -> product.getId() <= startId);
        return sortByCoords(collection);
    }

    public Vector<Product> update(int current_id, ProductModel element) throws NonExistingElementException {
        if (!collection.contains(collection.get((int) current_id))){
            throw new NonExistingElementException("Элемента с таким id не существует");
        }

        for (Product product : collection) {
            if (current_id == product.getId()){
                collection.remove(product);

                Product newElement = new Product(
                        current_id,
                        element.getName(),
                        element.getCoordinates(),
                        LocalDateTime.now(),
                        element.getPrice(),
                        element.getManufactureCost(),
                        element.getUnitOfMeasure(),
                        element.getOwner()
                );

                collection.add(newElement);
                break;
            }
        }
        return sortByCoords(collection);
    }

    public Vector<Product> printFieldDescendingUnitOfMeasure() {
        try {
            if (collection == null || collection.isEmpty()) {
                throw new EmptyFile();
            }

            Vector<Product> sortedProducts = new Vector<>(collection);
            sortedProducts.sort(Comparator.comparing(Product::getUnitOfMeasure).reversed());
            return sortedProducts;
        } catch (EmptyFile e) {
            Outputter.printerror("Коллекция пуста");
            return new Vector<>();
        }
    }

    public long countLessThanOwner(Person owner) {
        return collection.stream().filter(product -> product.getOwner().getName().compareTo(owner.getName()) < 0).count();
    }

    public Vector<Product> filterStartsWithName(String substring) throws NonExistingElementException {
        var filteredCollection = collection.stream()
                .filter(product -> product.getName().startsWith(substring))
                .collect(Collectors.toCollection(Vector::new));

        if (filteredCollection.isEmpty()){
            throw new NonExistingElementException("Элементов с таким именем не существует");
        }
        return sortByCoords(filteredCollection);
    }

    public Vector<Product> removeAt(Integer id) {
        for (Product product : collection) {
            if (product.getId() == id) {
                collection.remove(product);
                return sortByCoords(collection); // Найденный продукт удален, выходим из метода
            }
        }
        // Если продукт с данным id не был найден, можно сгенерировать исключение или просто вывести сообщение об ошибке
        throw new IllegalArgumentException("Product with id " + id + " not found");
    }

    private class CompareProduct implements Comparator<Product> {
        @Override
        public int compare(Product p1, Product p2) {
            return (int) (p1.getCoordinates().getX() - p2.getCoordinates().getX());
        }

        @Override
        public Comparator<Product> reversed() {
            return Comparator.super.reversed();
        }
    }

    private Vector<Product> sortByCoords(Vector<Product> collection) {
        return collection.stream().sorted(comparator).collect(Collectors.toCollection(Vector::new));
    }

    public Vector<Product> getProductCollections() {
        return collection;
    }
}
