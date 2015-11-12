/*
 * Copyright 2014-2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.Java8IntegrationTestBase;
import org.junit.Test;

public class Java8GenericTypesTest extends Java8IntegrationTestBase {
    @Test
    public void succeed_whenEqualsLooksAtObservableListFieldsGenericContent() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(JAVAFX_OBSERVABLELIST_CONTAINER_CLASS_NAME, JAVAFX_OBSERVABLELIST_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableMapFieldsGenericContent() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS_NAME, JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableSetFieldsGenericContent() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(JAVAFX_OBSERVABLESET_CONTAINER_CLASS_NAME, JAVAFX_OBSERVABLESET_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtListPropertyFieldsGenericContent() {
        if (!isJava8Available()) {
            return;
        }

        Class<?> type = compile(JAVAFX_LISTPROPERTY_CONTAINER_CLASS_NAME, JAVAFX_LISTPROPERTY_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
                .verify();
    }

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_OBSERVABLELIST_CONTAINER_CLASS_NAME = "JavaFXObservableListContainer";
    private static final String JAVAFX_OBSERVABLELIST_CONTAINER_CLASS =
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport javafx.collections.ObservableList;" +
            "\n" +
            "\npublic final class JavaFXObservableListContainer {" +
            "\n    private final ObservableList<Point> list;" +
            "\n    " +
            "\n    public JavaFXObservableListContainer(ObservableList<Point> list) {" +
            "\n        this.list = list;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXObservableListContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXObservableListContainer other = (JavaFXObservableListContainer)obj;" +
            "\n        if (list == null || other.list == null) {" +
            "\n            return list == other.list;" +
            "\n        }" +
            "\n        if (list.size() != other.list.size()) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        for (int i = 0; i < list.size(); i++) {" +
            "\n            Point x = list.get(i);" +
            "\n            Point y = other.list.get(i);" +
            "\n            if (!x.equals(y)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return true;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS_NAME = "JavaFXObservableMapContainer";
    private static final String JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS =
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport java.util.Map;" +
            "\nimport javafx.collections.ObservableMap;" +
            "\n" +
            "\npublic final class JavaFXObservableMapContainer {" +
            "\n    private final ObservableMap<Point, Point> map;" +
            "\n    " +
            "\n    public JavaFXObservableMapContainer(ObservableMap<Point, Point> map) {" +
            "\n        this.map = map;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXObservableMapContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXObservableMapContainer other = (JavaFXObservableMapContainer)obj;" +
            "\n        if (map == null || other.map == null) {" +
            "\n                return map == other.map;" +
            "\n        }" +
            "\n        if (map.size() != other.map.size()) {" +
            "\n                return false;" +
            "\n        }" +
            "\n        for (Map.Entry<Point, Point> e : map.entrySet()) {" +
            "\n                if (!other.map.containsKey(e.getKey())) {" +
            "\n                        return false;" +
            "\n                }" +
            "\n                if (!other.map.get(e.getKey()).equals(e.getValue())) {" +
            "\n                        return false;" +
            "\n                }" +
            "\n        }" +
            "\n        return true;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_OBSERVABLESET_CONTAINER_CLASS_NAME = "JavaFXObservableSetContainer";
    private static final String JAVAFX_OBSERVABLESET_CONTAINER_CLASS =
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport javafx.collections.ObservableSet;" +
            "\n" +
            "\npublic final class JavaFXObservableSetContainer {" +
            "\n    private final ObservableSet<Point> set;" +
            "\n    " +
            "\n    public JavaFXObservableSetContainer(ObservableSet<Point> set) {" +
            "\n        this.set = set;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXObservableSetContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXObservableSetContainer other = (JavaFXObservableSetContainer)obj;" +
            "\n        if (set == null || other.set == null) {" +
            "\n            return set == other.set;" +
            "\n        }" +
            "\n        if (set.size() != other.set.size()) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        for (Point p : set) {" +
            "\n            if (!other.set.contains(p)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return true;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    private static final String JAVAFX_LISTPROPERTY_CONTAINER_CLASS_NAME = "JavaFXListPropertyContainer";
    private static final String JAVAFX_LISTPROPERTY_CONTAINER_CLASS =
            "\nimport javafx.beans.property.ListProperty;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport java.util.Objects;" +
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\n" +
            "\npublic final class JavaFXListPropertyContainer {" +
            "\n    private final ListProperty<Point> p;" +
            "\n    " +
            "\n    public JavaFXListPropertyContainer(ListProperty<Point> p) {" +
            "\n        this.p = p;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXListPropertyContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXListPropertyContainer other = (JavaFXListPropertyContainer)obj;" +
            "\n        if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {" +
            "\n            Point x = p.getValue().get(0);" +
            "\n            Point y = other.p.getValue().get(0);" +
            "\n            if (!x.equals(y)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return Objects.equals(p, other.p);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";
}
