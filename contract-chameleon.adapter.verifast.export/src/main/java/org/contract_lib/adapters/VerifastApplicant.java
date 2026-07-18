package org.contract_lib.adapters;

import org.contract_lib.contract_chameleon.Adapter;
import org.contract_lib.contract_chameleon.adapters.ExportAdapter;
import org.contract_lib.contract_chameleon.contexts.SourcePathsContext;
import org.contract_lib.contract_chameleon.contexts.ResultDirectoryContext.Dir;

import org.contract_lib.lang.contract_lib.context_provider.ContractLibAstContextProvider;
import org.contract_lib.lang.contract_lib.context_provider.FileIdentifierContextProvider;
import org.contract_lib.lang.contract_lib.contexts.ContractLibAstContext;
import org.contract_lib.lang.contract_lib.contexts.CurrentFileIdentifierContext;

import com.google.auto.service.AutoService;

@AutoService(Adapter.class)
public final class VerifastApplicant extends ExportAdapter {

  public String getAdapterName() {
    return "verifast-applicant";
  }

  @Override
  public void performForPath(Dir finalDir) {
    // TODO: Remove unchecked unwrap
    SourcePathsContext sources = getContext(SourcePathsContext.class).get();

    // TODO: Remove unchecked unwrap
    CurrentFileIdentifierContext identifierContext = getContext(new FileIdentifierContextProvider()).get();
    identifierContext.setFileIdentifier(sources.getPaths().getFirst());

    // TODO: Remove unchecked unwrap
    ContractLibAstContext contract_lib_context = this.getContext(new ContractLibAstContextProvider()).get();

    SimpleVerifastTranslator trans = new SimpleVerifastTranslator(
        identifierContext.getFilePath(),
        getMessageContext().getMessageManager(),
        this.getMessageContext());

    trans.translateContractLibAstApplicant(contract_lib_context.getAst())
        .forEach(finalDir::writeResult);
  }
}
