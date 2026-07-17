package org.contract_lib.lang.contract_lib.tester;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.contract_lib.contract_chameleon.SharedContextManager;
import org.contract_lib.contract_chameleon.adapters.CheckerAdapter.CheckerAdapterResult;
import org.contract_lib.contract_chameleon.contexts.MessageContext;
import org.contract_lib.lang.contract_lib.ast.ContractLibAstElement;
import org.contract_lib.lang.contract_lib.contexts.ast_extensions.AvailableSortIdentifierContext;
import org.contract_lib.lang.contract_lib.label.Identifier;
import org.contract_lib.lang.contract_lib.label.IdentifierMode.Accessed;
import org.contract_lib.lang.contract_lib.label.IdentifierMode.Defined;
import org.contract_lib.lang.contract_lib.label.IdentifierScope.Global;
import org.contract_lib.lang.contract_lib.label.IdentifierScope.Local;
import org.contract_lib.lang.contract_lib.label.IdentifierType.SortIdentifier;
import org.contract_lib.lang.contract_lib.translator_extensions.AccSortIdentifierExtractor;
import org.contract_lib.lang.contract_lib.translator_extensions.DefSortIdentifierExtractor;

public class TestSorts {

  private final MessageContext messageContext;

  private AccSortIdentifierExtractor accSortIdentifierExtractor;
  private DefSortIdentifierExtractor defSortIdentifierExtractor;

  private Set<String> availableIdentifiers;

  //TODO: Add static symbols that can appear as identifer, remove from extractors.
  // private final … theory sorts

  private AvailableSortIdentifierContext availableSortIdentifierContext;

  public TestSorts(MessageContext messageContext) {
    this.messageContext = messageContext;
  }

  public CheckerAdapterResult testSorts(
      SharedContextManager sharedContextManager) {

    //accVariableIdentifierExtractor = //TODO: Load from shared context
    //availableVariableIdentifierContext = //TODO: Load from shared context

    availableIdentifiers = availableSortIdentifierContext.allIdentifier().identifier();

    List<Supplier<CheckerAdapterResult>> variableChecks = List.of(
        this::testVariableAccess,
        this::testVariableDef);

    return variableChecks
        .stream()
        .map(Supplier::get)
        .reduce(CheckerAdapterResult.SUCCESS,
            CheckerAdapterResult::and);
  }

  /// Using the extreactors of this class, test that all variable definitions are valid (they shadow nothing).
  private CheckerAdapterResult testVariableDef() {
    return defSortIdentifierExtractor
        .allEntries()
        .stream()
        .map(this::checkAstSortDef)
        .reduce(CheckerAdapterResult.SUCCESS,
            CheckerAdapterResult::and);
  }

  /** 
   * Check for an node defining variable identifiers if they are valid,
   * logs error if this is not so.
   */
  private CheckerAdapterResult checkAstSortDef(
      Entry<ContractLibAstElement, Identifier<Defined, Global, SortIdentifier>> entry) {
    ContractLibAstElement node = entry.getKey();

    Set<String> definedIdentifiers = entry.getValue().identifier();

    return definedIdentifiers.stream()
        .map((i) -> {
          if (availableIdentifiers.contains(i)) {
            return CheckerAdapterResult.SUCCESS;
          } else {

            //TODO: Better log message
            messageContext.logError(String.format("Identifier '%s' is redefined at node: ", i, node));
            return CheckerAdapterResult.FAILURE;
          }
        })
        .reduce(CheckerAdapterResult.SUCCESS,
            CheckerAdapterResult::and);
  }

  /// Using the extreactors of this class, test that all variable accesses are valid, the variable is defined.
  private CheckerAdapterResult testVariableAccess() {
    return accSortIdentifierExtractor
        .allEntries()
        .stream()
        .map(this::checkAstNodeVariableAccess)
        .reduce(CheckerAdapterResult.SUCCESS,
            CheckerAdapterResult::and);
  }

  /// Check an entry if all identifer  are available and logs error if not so.
  private CheckerAdapterResult checkAstNodeVariableAccess(
      Entry<ContractLibAstElement, Identifier<Accessed, Local, SortIdentifier>> entry) {
    ContractLibAstElement node = entry.getKey();
    Set<String> accessedIdentifiers = entry.getValue().identifier();

    return accessedIdentifiers
        .stream()
        .map((i) -> {
          if (availableIdentifiers.contains(i)) {
            return CheckerAdapterResult.SUCCESS;
          } else {

            //TODO: Better log message
            messageContext.logError(String.format("Identifier '%s' not available at node: ", i, node));
            return CheckerAdapterResult.FAILURE;
          }
        })
        .reduce(CheckerAdapterResult.SUCCESS,
            CheckerAdapterResult::and);
  }
}
