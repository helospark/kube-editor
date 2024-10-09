package com.helospark.kubeeditor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

public class KubeCompletionProcessorTest {
    private static KubeCompletionProcessor underTest;
    @Mock
    private IDocument document;
    @Mock
    private ITextViewer viewer;

    @BeforeClass
    public static void beforeClass() {
        underTest = new KubeCompletionProcessor();
    }

    @Before
    public void setUp() {
        initMocks(this);

        when(viewer.getDocument()).thenReturn(document);
    }

    @Test
    public void testValueAtSecondLevel() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  na";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("name", proposals[0].getDisplayString());
    }

    @Test
    public void testValueAtFirstLevel() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "meta";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("metadata", proposals[0].getDisplayString());
    }

    @Test
    public void testValueManyLevelsDown() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labe";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("labels", proposals[0].getDisplayString());
    }

    @Test
    public void testServiceLoadBalanderIp() {
        String testData = "" +
                "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  name: my-service\n" +
                "spec:\n" +
                "  selector:\n" +
                "    app: MyApp\n" +
                "  ports:\n" +
                "  - protocol: TCP\n" +
                "    port: 80\n" +
                "    targetPort: 9376\n" +
                "  clusterIP: 10.0.171.239\n" +
                "  loadBalan";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("loadBalancerIP", proposals[0].getDisplayString());
    }

    @Test
    public void testServiceLoadBalanderIpWithWindowsFileEndings() {
        String testData = "" +
                "apiVersion: v1\r\n" +
                "kind: Service\r\n" +
                "metadata:\r\n" +
                "  name: my-service\r\n" +
                "spec:\r\n" +
                "  selector:\r\n" +
                "    app: MyApp\r\n" +
                "  ports:\r\n" +
                "  - protocol: TCP\r\n" +
                "    port: 80\r\n" +
                "    targetPort: 9376\r\n" +
                "  clusterIP: 10.0.171.239\r\n" +
                "  loadBalan";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("loadBalancerIP", proposals[0].getDisplayString());
    }

    @Test
    public void testServiceList() {
        String testData = "" +
                "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  name: my-service\n" +
                "spec:\n" +
                "  selector:\n" +
                "    app: MyApp\n" +
                "  ports:\n" +
                "  - proto";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("protocol", proposals[0].getDisplayString());
    }

    @Test
    public void testServicePort() {
        String testData = "" +
                "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  name: my-service\n" +
                "spec:\n" +
                "  selector:\n" +
                "    app: MyApp\n" +
                "  por";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("ports", proposals[0].getDisplayString());
    }

    @Test
    public void testServicePortWithWindowsLineEndings() {
        String testData = "" +
                "apiVersion: v1\r\n" +
                "kind: Service\r\n" +
                "metadata:\r\n" +
                "  name: my-service\r\n" +
                "spec:\r\n" +
                "  selector:\r\n" +
                "    app: MyApp\r\n" +
                "  por";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals("ports", proposals[0].getDisplayString());
    }

    @Test
    public void testValueWhenCalledInsideAMap() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: my-nginx\n" +
                "spec:\n" +
                "  selector:\n" +
                "    matchLabels:\n" +
                "      run: my-nginx\n" +
                "  replicas: 2\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        ru";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length() - 1);

        assertEquals(0, proposals.length);
    }

    @Test
    public void testBooleanValue() {
        String testData = "" +
                "apiVersion: apps/v1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  name: replaceme\n" +
                "  labels:\n" +
                "    app: replaceme\n" +
                "spec:\n" +
                "  replicas: 1\n" +
                "  paused: f";

        when(document.get()).thenReturn(testData);

        ICompletionProposal[] proposals = underTest.computeCompletionProposals(viewer, testData.length());

        assertEquals("false", proposals[0].getDisplayString());
    }

}
