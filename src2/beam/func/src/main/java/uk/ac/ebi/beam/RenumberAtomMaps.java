package uk.ac.ebi.beam;

final class RenumberAtomMaps {

    private static final class State {
        Graph g;
        boolean[] visit;
        int[] map;
        int   nMaps;

        public State(Graph g, int maxidx) {
            this.g = g;
            this.visit = new boolean[g.order()];
            this.map = new int[maxidx + 1];
            this.nMaps = 0;
        }
    }

    private static void traverse(State s, int idx) {
        s.visit[idx] = true;
        int mapIdx = s.g.atom(idx).atomClass();
        if (mapIdx != 0) {
            if (s.map[mapIdx] == 0)
                s.map[mapIdx] = ++s.nMaps;
            mapIdx = s.map[mapIdx];
            s.g.setAtom(idx, AtomBuilder.fromExisting(s.g.atom(idx)).atomClass(mapIdx).build());
        }
        for (Edge e : s.g.edges(idx)) {
            int nbr = e.other(idx);
            if (!s.visit[nbr])
                traverse(s, nbr);
        }
    }

    static void renumber(Graph g) {
        int maxMapIdx = 0;
        for (int i = 0; i < g.order(); i++)
            maxMapIdx = Math.max(maxMapIdx, g.atom(i).atomClass());
        if (maxMapIdx == 0)
            return;
        State state = new State(g, maxMapIdx);
        for (int i = 0; i < g.order(); i++)
            if (!state.visit[i])
                traverse(state, i);
    }
}
