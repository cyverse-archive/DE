---
layout: page
title: DE API Documentation
---

# Table of Contents

* [Categorizing Apps with Ontology Hierarchies](#categorizing-apps-with-ontology-hierarchies)
    * [Save an Ontology XML Document](#save-an-ontology-xml-document)
    * [Listing Saved Ontology Details](#listing-saved-ontology-details)
    * [Logically Deleting an Ontology](#logically-deleting-an-ontology)
    * [Set Active Ontology Version](#set-active-ontology-version)
    * [Save an Ontology Hierarchy](#save-an-ontology-hierarchy)
    * [Deleting an Ontology Hierarchy](#deleting-an-ontology-hierarchy)
    * [Listing Ontology Hierarchies](#listing-ontology-hierarchies)
    * [Listing Filtered Ontology Hierarchies](#listing-filtered-ontology-hierarchies)
    * [Listing Hierarchies for any Ontology](#listing-hierarchies-for-any-ontology)
    * [Listing Filtered Hierarchies for any Ontology](#listing-filtered-hierarchies-for-any-ontology)
    * [Listing Apps in Ontology Hierarchies](#listing-apps-in-ontology-hierarchies)
    * [Listing Unclassified Apps for the Active Ontology](#listing-unclassified-apps-for-the-active-ontology)
    * [Listing Unclassified Apps for any Ontology](#listing-unclassified-apps-for-any-ontology)

# Categorizing Apps with Ontology Hierarchies

1. First an admin must upload an ontology XML document,
   which will be stored in the metadata database,
   and later parsed with the OWLAPI libraries in order to build the app categories.
    * [Save an Ontology XML Document](#save-an-ontology-xml-document)
2. Once 1 or more ontology XML documents have been uploaded,
   the details of each available ontology may be listed.
   Each uploaded document is stored with a unique "version" ID.
   This version ID is used in the remaining endpoints in order to save and list ontology classes and
   hierarchies parsed from the associated ontology.
    * [Listing Saved Ontology Details](#listing-saved-ontology-details)
    * Ontologies may be removed from this listing by marking them as deleted:
        * [Logically Deleting an Ontology](#logically-deleting-an-ontology)
3. Next an admin must choose which hierarchies (and the ontology classes in them)
   will be available for use in the DE.
    * [Save an Ontology Hierarchy](#save-an-ontology-hierarchy)
    * An admin may delete all classes under any ontology class hierarchy
      which will allow the admin to selectively remove sub-hierarchies,
      or the entire hierarchy from the root.
        * [Deleting an Ontology Hierarchy](#deleting-an-ontology-hierarchy)
        * Deleted classes and sub-hierarchies can easily be re-added by re-saving the hierarchy root
          (only classes and hierarchies that are not already saved under the given root are added).
4. Finally, the admin sets an ontology version as the default,
   `active` version used by the DE when listing hierarchies or ontology classes for regular users.
    * [Set Active Ontology Version](#set-active-ontology-version)
    * This active version is used by the apps service in the following endpoints:
        * [Listing Ontology Hierarchies](#listing-ontology-hierarchies)
        * [Listing Filtered Ontology Hierarchies](#listing-filtered-ontology-hierarchies)
        * [Listing Unclassified Apps for the Active Ontology](#listing-unclassified-apps-for-the-active-ontology)
5. Admins or users with app "write" permissions may categorize those apps under ontology classes
   by attaching the class IRIs as metadata.
    * [Managing App AVU Metadata](app-metadata.html#managing-app-avu-metadata)
6. Apps with an ontology class attached as metadata (regardless of ontology version)
   may be listed with the following endpoint:
    * [Listing Apps in Ontology Hierarchies](#listing-apps-in-ontology-hierarchies)
7. An admin may add hierarchies for other ontology versions
   and preview their filtered hierarchies and app listings,
   without affecting the active version in use by normal users.
    * [Listing Hierarchies for any Ontology](#listing-hierarchies-for-any-ontology)
    * [Listing Filtered Hierarchies for any Ontology](#listing-filtered-hierarchies-for-any-ontology)
    * [Listing Unclassified Apps for any Ontology](#listing-unclassified-apps-for-any-ontology)

## Save an Ontology XML Document

Secured Endpoint: POST /admin/ontologies

Delegates to metadata: POST /admin/ontologies

This endpoint is a passthrough to the metadata endpoint using the same path.
Please see the metadata service documentation for more information.

## Listing Saved Ontology Details

Secured Endpoint: GET /admin/ontologies

Delegates to apps: GET /admin/ontologies

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Logically Deleting an Ontology

Secured Endpoint: DELETE /admin/ontologies/{ontology-version}

Delegates to apps: DELETE /admin/ontologies/{ontology-version}

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Set Active Ontology Version

Secured Endpoint: POST /admin/ontologies/{ontology-version}

Delegates to apps: POST /admin/ontologies/{ontology-version}

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Save an Ontology Hierarchy

Secured Endpoint: PUT /admin/ontologies/{ontology-version}/{root-iri}

Delegates to metadata: PUT /admin/ontologies/{ontology-version}/{root-iri}

This endpoint is a passthrough to the metadata endpoint using the same path.
Please see the metadata service documentation for more information.

## Deleting an Ontology Hierarchy

Secured Endpoint: DELETE /admin/ontologies/{ontology-version}/{root-iri}

Delegates to metadata: DELETE /admin/ontologies/{ontology-version}/{root-iri}

This endpoint is a passthrough to the metadata endpoint using the same path.
Please see the metadata service documentation for more information.

## Listing Ontology Hierarchies

Secured Endpoint: GET /apps/hierarchies

Delegates to apps: GET /apps/hierarchies

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Listing Filtered Ontology Hierarchies

Secured Endpoint: GET /apps/hierarchies/{root-iri}

Delegates to apps: GET /apps/hierarchies/{root-iri}

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Listing Hierarchies for any Ontology

Secured Endpoint: GET /admin/ontologies/{ontology-version}

Delegates to metadata: GET /ontologies/{ontology-version}

This endpoint is a passthrough to the metadata endpoint using the same path.
Please see the metadata service documentation for more information.

## Listing Filtered Hierarchies for any Ontology

Secured Endpoint: GET /admin/ontologies/{ontology-version}/{root-iri}

Delegates to apps: GET /admin/ontologies/{ontology-version}/{root-iri}

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Listing Apps in Ontology Hierarchies

Secured Endpoint: GET /apps/hierarchies/{class-iri}/apps

Delegates to apps: GET /apps/hierarchies/{class-iri}/apps

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Listing Unclassified Apps for the Active Ontology

Secured Endpoint: GET /apps/hierarchies/{root-iri}/unclassified

Delegates to apps: GET /apps/hierarchies/{root-iri}/unclassified

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.

## Listing Unclassified Apps for any Ontology

Secured Endpoint: GET /admin/ontologies/{ontology-version}/{root-iri}/unclassified

Delegates to apps: GET /admin/ontologies/{ontology-version}/{root-iri}/unclassified

This endpoint is a passthrough to the apps endpoint using the same path.
Please see the apps service documentation for more information.
