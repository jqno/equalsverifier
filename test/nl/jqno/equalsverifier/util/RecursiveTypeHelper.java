/*
 * Copyright 2010 Jan Ouwens
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
package nl.jqno.equalsverifier.util;

public class RecursiveTypeHelper {
	public static final class Node {
		Node node;
	}
	
	static final class NodeArray {
		NodeArray[] nodeArrays;
	}
	
	static final class TwoStepNodeA {
		TwoStepNodeB node;
	}
	
	static final class TwoStepNodeB {
		TwoStepNodeA node;
	}
	
	static final class TwoStepNodeArrayA {
		TwoStepNodeArrayB[] nodes;
	}
	
	static final class TwoStepNodeArrayB {
		TwoStepNodeArrayA[] nodes;
	}
	
	static final class NotRecursiveA {
		NotRecursiveB b;
		NotRecursiveC c;
	}
	
	static final class NotRecursiveB {
		NotRecursiveD d;
	}
	
	static final class NotRecursiveC {
		NotRecursiveD d;
	}
	
	static final class NotRecursiveD {}
}
