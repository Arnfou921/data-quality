// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.broadcast;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;

/**
 * Created by jteuladedenantes on 20/10/16.
 * <p>
 * This class uses the singleton pattern to avoid creating the lucene index for each row. The lucene index will be created for
 * each worker.
 */
public class BroadcastIndexObject implements Serializable {

    private static final long serialVersionUID = 7350930198992853600L;

    private static final Logger LOGGER = Logger.getLogger(BroadcastIndexObject.class);

    // The serializable object
    private List<BroadcastDocumentObject> documentList;

    // The lucene index created from the serializable object
    private Directory ramDirectory;

    /**
     * Build an index based on a list of {@link BroadcastDocumentObject}.
     * 
     * @param documentList The {@link BroadcastDocumentObject} to be used to build up the index.
     */
    public BroadcastIndexObject(List<BroadcastDocumentObject> documentList) {
        this.documentList = documentList;
    }

    /**
     * Constructor
     * 
     * @param inputDirectory
     */
    public BroadcastIndexObject(Directory inputDirectory) {
        this(inputDirectory, false);
    }

    /**
     * Constructor
     * 
     * @param inputDirectory
     * @param includeOpenCategories whether open categories should be included
     */
    public BroadcastIndexObject(Directory inputDirectory, boolean includeOpenCategories) {
        try {
            if (includeOpenCategories) {
                documentList = BroadcastUtils.readDocumentsFromIndex(inputDirectory);
            } else {
                Collection<DQCategory> cats = CategoryRegistryManager.getInstance().listCategories(false);
                Set<String> catNames = new HashSet<String>();
                for (DQCategory dqCat : cats) {
                    if (CategoryType.DICT.equals(dqCat.getType())) {
                        catNames.add(dqCat.getName());
                    }
                }
                documentList = BroadcastUtils.readDocumentsFromIndex(inputDirectory, catNames);
            }
        } catch (IOException e) {
            documentList = Collections.emptyList();
            LOGGER.error("Unable to read synonym index.", e);
        }
    }

    public List<BroadcastDocumentObject> getDocumentList() {
        return documentList;
    }

    /**
     * The singleton method which creates the lucene index if necessary.
     * 
     * @return the lucene index
     */
    public synchronized Directory get() {
        if (ramDirectory == null) {
            try {
                ramDirectory = BroadcastUtils.createRamDirectoryFromDocuments(documentList);
            } catch (IOException e) {
                LOGGER.error("Unable to rebuild the broadcast dictionary.", e);
            }
        }
        return ramDirectory;
    }

}
