/*******************************************************************************
 * Copyright (c) 2006, 2007 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.diff.generic.merge.impl;

import java.util.Iterator;

import org.eclipse.emf.compare.EMFComparePlugin;
import org.eclipse.emf.compare.FactoryException;
import org.eclipse.emf.compare.diff.merge.api.DefaultMerger;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChangeRightTarget;
import org.eclipse.emf.compare.util.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Merger for an {@link ModelElementChangeRightTarget} operation.<br/>
 * <p>
 * Are considered for this merger :
 * <ul>
 * <li>{@link AddModelElement}</li>
 * <li>{@link RemoteRemoveModelElement}</li>
 * </ul>
 * </p>
 * 
 * @author Cedric Brun <a href="mailto:cedric.brun@obeo.fr">cedric.brun@obeo.fr</a>
 */
public class ModelElementChangeRightTargetMerger extends DefaultMerger {
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.diff.merge.api.AbstractMerger#applyInOrigin()
	 */
	@Override
	public void applyInOrigin() {
		/*
		 * FIXME [bug #209521] if we're merging a ModelElement using UnmatchedElements (suppose an EPackage
		 * containing an EDatatype T and an EClass with an attribute of type T), hard-links are done between
		 * the two models.
		 */
		final ModelElementChangeRightTarget theDiff = (ModelElementChangeRightTarget)this.diff;
		final EObject origin = theDiff.getLeftParent();
		final EObject element = theDiff.getRightElement();
		final EObject newOne = EcoreUtil.copy(element);
		final EReference ref = element.eContainmentFeature();
		if (ref != null) {
			try {
				EFactory.eAdd(origin, ref.getName(), newOne);
				copyXMIID(element, newOne);
			} catch (FactoryException e) {
				EMFComparePlugin.log(e, true);
			}
		} else {
			findLeftResource().getContents().add(newOne);
		}
		// we should now have a look for AddReferencesLinks needing this object
		final Iterator<EObject> siblings = getDiffModel().eAllContents();
		while (siblings.hasNext()) {
			final DiffElement op = (DiffElement)siblings.next();
			if (op instanceof ReferenceChangeRightTarget) {
				final ReferenceChangeRightTarget link = (ReferenceChangeRightTarget)op;
				// now if I'm in the target References I should put my copy in the origin
				if (link.getRightAddedTarget().equals(element)) {
					link.setLeftAddedTarget(newOne);
				}
			}
		}
		super.applyInOrigin();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.diff.merge.api.AbstractMerger#undoInTarget()
	 */
	@Override
	public void undoInTarget() {
		/*
		 * FIXME [bug #209521] if we're merging a ModelElement using UnmatchedElements (suppose an EPackage
		 * containing an EDatatype T and an EClass with an attribute of type T), hard-links are done between
		 * the two models.
		 */
		final ModelElementChangeRightTarget theDiff = (ModelElementChangeRightTarget)this.diff;
		final EObject element = theDiff.getRightElement();
		final EObject parent = theDiff.getRightElement().eContainer();
		EcoreUtil.remove(element);
		// now removes all the dangling references
		removeDanglingReferences(parent);
		super.undoInTarget();
	}
}
