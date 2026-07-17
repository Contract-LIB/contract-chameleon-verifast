package org.contract_lib.adapters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.contract_lib.contract_chameleon.Adapter;
import org.contract_lib.contract_chameleon.adapters.ExportAdapter;
import org.contract_lib.contract_chameleon.contexts.ResultDirectoryContext.Dir;
import org.contract_lib.lang.contract_lib.context_provider.ContractLibAstContextProvider;
import org.contract_lib.lang.contract_lib.contexts.ContractLibAstContext;

import com.google.auto.service.AutoService;

@AutoService(Adapter.class)
public final class VerifastProvider extends ExportAdapter {

  public String getAdapterName() {
    return "verifast-provider";
  }

  @Override
  public void performForPath(Path p, Dir finalDir) {
    try {

      Optional<ContractLibAstContext> contract_lib_context = this.getContext(new ContractLibAstContextProvider(p));

      SimpleVerifastTranslator trans = new SimpleVerifastTranslator(p,
          getMessageContext().getMessageManager(),
          this.getMessageContext());

      contract_lib_context.ifPresentOrElse(
          (c) -> trans.translateContractLibAstProvider(c.getAst())
              .forEach(finalDir::writeResult),
          this::missingContext);

    } catch (IOException e) {
      getMessageContext().logException(e);
    }
  }

  public void missingContext() {
    // TODO:
  }
}
