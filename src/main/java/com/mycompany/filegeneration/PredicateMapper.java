package com.mycompany.filegeneration;

import java.util.*;

public class PredicateMapper {

    private static final Map<String, String> COLUMN_PREDICATES = new HashMap<>();
    private static final Set<String> DATE_TYPES = Set.of("date", "timestamp", "datetime");
    private static final Set<String> NUMERIC_TYPES = Set.of("decimal", "numeric", "float", "double");
    private static final Set<String> INT_TYPES = Set.of("int", "smallint", "integer");

    static {
        COLUMN_PREDICATES.put("nombre", "foaf:name");
        COLUMN_PREDICATES.put("email", "ex:email");
        COLUMN_PREDICATES.put("telefono", "foaf:phone");
        COLUMN_PREDICATES.put("direccion", "schema:address");
        COLUMN_PREDICATES.put("ciudad", "schema:addressLocality");
        COLUMN_PREDICATES.put("pais", "schema:addressCountry");
        COLUMN_PREDICATES.put("codigo_postal", "schema:postalCode");
        COLUMN_PREDICATES.put("descripcion", "schema:description");
        COLUMN_PREDICATES.put("id", "dct:identifier");
        COLUMN_PREDICATES.put("url", "schema:url");
        COLUMN_PREDICATES.put("categoria", "ex:id_categoria");
        COLUMN_PREDICATES.put("sexo", "ex:sexo");
    }

    public static String getPredicate(String columnName, String sqlType) {
        String lowerCol = columnName.toLowerCase();
        String lowerType = sqlType.toLowerCase();

        // Si la columna está en el mapeo fijo, devolver el predicado correspondiente
        if (COLUMN_PREDICATES.containsKey(lowerCol)) {
            return COLUMN_PREDICATES.get(lowerCol);
        }

        // Manejo flexible de fechas
        if (DATE_TYPES.contains(lowerType)) {
            return "schema:date" + capitalizeWords(lowerCol.replace("fecha_", ""));
        }

        // Manejo flexible de precios
        if (lowerCol.contains("precio") && NUMERIC_TYPES.contains(lowerType)) {
            return "ex:price" + capitalizeWords(lowerCol.replace("precio_", ""));
        }

        // Manejo de enteros
        if (INT_TYPES.contains(lowerType)) {
            if (lowerCol.equals("id") || lowerCol.equals("codigo")) {
                return "schema:identifier";
            } else if (lowerCol.contains("edad") || lowerCol.contains("años") || lowerCol.contains("anio")) {
                return "schema:age";
            } else if (lowerCol.contains("cantidad") || lowerCol.contains("nivel")) {
                return "schema:inventoryLevel";
            }
        }

        // Si no coincide con nada, usar un prefijo genérico
        return "ex:" + lowerCol.replace("_", "");
    }

    public static String getClassForTable(String tableName) {
        String lowerTable = tableName.toLowerCase();

        if (lowerTable.contains("cliente") || lowerTable.contains("personas") || lowerTable.contains("usuario")) {
            return "schema:Person";
        } else if (lowerTable.contains("producto") || lowerTable.contains("item")) {
            return "schema:Product";
        } else if (lowerTable.contains("orden") || lowerTable.contains("factura")) {
            return "schema:Order";
        } else if (lowerTable.contains("empresa") || lowerTable.contains("compania")) {
            return "schema:Organization";
        } else if (lowerTable.contains("categoria")) {
            return "schema:Category";
        }

        return "ex:" + tableName.replace("_", "");
    }

    private static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        String[] words = str.split("_");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                           .append(word.substring(1));
            }
        }
        return capitalized.toString();
    }
}