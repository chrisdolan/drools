package org.drools.xml;

import java.util.HashSet;

import org.drools.process.core.Process;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.StartNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StoreHandler extends BaseAbstractHandler
    implements
    Handler {
    public StoreHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet();            
            this.validPeers.add( StartNode.class );
            this.validPeers.add( ActionNode.class );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) xmlPackageReader.getParent();
        
        ActionNode actionNode = new ActionNode();
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, xmlPackageReader );        
        actionNode.setName( name );
        
        process.addNode( actionNode );
        ((ProcessBuildData)xmlPackageReader.getData()).addNode( actionNode );
        
        return actionNode;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Configuration config = xmlPackageReader.endConfiguration();
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) xmlPackageReader.getParent();

        ActionNode actionNode = ( ActionNode ) xmlPackageReader.getCurrent();
        
        String text = config.getText();
        
        DroolsConsequenceAction actionText = new DroolsConsequenceAction( "mvel", "list.add(\"" + text + "\")" );
        
        actionNode.setAction( actionText );
        
        return actionNode;
    }

    public Class generateNodeFor() {
        return ActionNode.class;
    }    

}
