package org.apache.sedona.core.dbscanJudgement;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class UnionFindImpl implements UnionFind {
    private List<Integer> clusters;
    private List<Integer> clusterSizes;
    private int numClusters;
    private final int n;

    public UnionFindImpl(int n) {
        this.n = n;
        this.numClusters = n;
        this.clusters = new ArrayList<>();
        this.clusterSizes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            clusters.add(i);
            clusterSizes.add(1);
        }
    }

    @Override
    public int find(int i) {
        int base = i;
        while (clusters.get(base) != base) {
            base = clusters.get(base);
        }
        while (i != base) {
            int next = clusters.get(i);
            clusters.set(i, base);
            i = next;
        }
        return i;
    }

    @Override
    public int size(int i) {
        return clusterSizes.get(find(i));
    }

    @Override
    public void union(int i, int j) {
        int a = find(i);
        int b = find(j);
        if (a == b) {
            return;
        }
        if (clusterSizes.get(a) < clusterSizes.get(b) ||
                (Objects.equals(clusterSizes.get(a), clusterSizes.get(b)) && a > b)) {
            clusters.set(a, clusters.get(b));
            clusterSizes.set(b, clusterSizes.get(a) + clusterSizes.get(b));
            clusterSizes.set(a, 0);
        } else {
            clusters.set(b, clusters.get(a));
            clusterSizes.set(a, clusterSizes.get(a) + clusterSizes.get(b));
            clusterSizes.set(b, 0);
        }
        numClusters--;
    }

    @Override
    public Integer[] orderedByCluster() {
        Integer[] clusterIdByElemId = new Integer[n];
        for (int i = 0; i < n; i++) {
            find(i);
            clusterIdByElemId[i] = clusters.get(i);
        }
        Arrays.sort(clusterIdByElemId);
        return clusterIdByElemId;
    }

    @Override
    public Integer[] getCollapsedClusterIds() {
        Integer[] orderedComponents = orderedByCluster();
        Integer[] newIds = new Integer[n];
        int lastOldId = find(orderedComponents[0]), currentNewId = 0;

        for (int i = 0; i < n; i++) {
            int j = orderedComponents[i];
            int currentOldId = find(j);
            if (currentOldId != lastOldId) {
                currentNewId++;
            }
            newIds[i] = currentNewId;
            lastOldId = currentOldId;
        }
        return newIds;
    }
}
