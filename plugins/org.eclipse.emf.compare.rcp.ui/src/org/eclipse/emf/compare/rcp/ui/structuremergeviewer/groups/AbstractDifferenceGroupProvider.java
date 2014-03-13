/*******************************************************************************
 * Copyright (c) 2013, 2014 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.rcp.ui.structuremergeviewer.groups;

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.edit.tree.TreeNode;
import org.eclipse.emf.edit.tree.TreePackage;

/**
 * Abstract implementation of {@link IDifferenceGroupProvider}.
 * 
 * @author <a href="mailto:mikael.barbero@obeo.fr">Mikael Barbero</a>
 * @since 4.0
 */
public abstract class AbstractDifferenceGroupProvider extends AdapterImpl implements IDifferenceGroupProvider {

	/** The cross reference adapter used by the difference group provider. */
	private final ECrossReferenceAdapter crossReferenceAdapter;

	/**
	 * Default constructor.
	 */
	public AbstractDifferenceGroupProvider() {
		crossReferenceAdapter = new ECrossReferenceAdapter() {
			/**
			 * {@inheritDoc}
			 * 
			 * @see org.eclipse.emf.ecore.util.ECrossReferenceAdapter#isIncluded(org.eclipse.emf.ecore.EReference)
			 */
			@Override
			protected boolean isIncluded(EReference eReference) {
				return eReference == TreePackage.Literals.TREE_NODE__DATA;
			}
		};
	}

	/**
	 * Returns the cross reference adapter used by this difference group provider.
	 * 
	 * @return the crossReferenceAdapter the cross reference adapter used by this difference group provider.
	 */
	protected final ECrossReferenceAdapter getCrossReferenceAdapter() {
		return crossReferenceAdapter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.rcp.ui.structuremergeviewer.groups.IDifferenceGroupProvider#getTreeNodes(java.lang.Object)
	 */
	public List<TreeNode> getTreeNodes(EObject eObject) {
		Collection<Setting> inverseReferences = crossReferenceAdapter
				.getNonNavigableInverseReferences(eObject);
		List<TreeNode> ret = newArrayListWithCapacity(inverseReferences.size());
		for (Setting setting : inverseReferences) {
			ret.add((TreeNode)setting.getEObject());
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	@Override
	public boolean isAdapterForType(Object type) {
		return type == IDifferenceGroupProvider.class;
	}
}
