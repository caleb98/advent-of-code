package net.calebscode.aoc.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Graph<T> {

	private Map<Vertex, Set<Vertex>> adjacencies;

	public Graph() {
		adjacencies = new HashMap<Vertex, Set<Vertex>>();
	}

	public void addVertex(T data) {
		Vertex vertex = new Vertex(data);
		if (!adjacencies.containsKey(vertex)) {
			adjacencies.put(vertex, new HashSet<>());
		}
	}

	public class Vertex {

		private T data;

		private Vertex(T data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			return Objects.hash(data);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || !getClass().equals(obj.getClass())) return false;

			Vertex other = (Vertex) obj;
			return Objects.equals(data, other.data);
		}

	}

}
