/*
 * This file is part of TabTPS, licensed under the MIT License.
 *
 * Copyright (c) 2020-2021 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.tabtps.fabric.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.fabric.argument.server.MultiplePlayerSelectorArgument;
import cloud.commandframework.fabric.data.MultiplePlayerSelector;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.tabtps.common.command.Commander;
import xyz.jpenilla.tabtps.common.command.Commands;
import xyz.jpenilla.tabtps.common.command.commands.PingCommand;
import xyz.jpenilla.tabtps.fabric.TabTPSFabric;

import java.util.stream.Collectors;

public final class FabricPingCommand extends PingCommand {
  private final TabTPSFabric tabTPSFabric;

  public FabricPingCommand(final @NonNull TabTPSFabric tabTPSFabric, final @NonNull Commands commands) {
    super(tabTPSFabric.tabTPS(), commands);
    this.tabTPSFabric = tabTPSFabric;
  }

  @Override
  public void register() {
    this.registerPingTargetsCommand(MultiplePlayerSelectorArgument.of("target"), this::handlePingTargets);
  }

  private void handlePingTargets(final @NonNull CommandContext<Commander> context) {
    final MultiplePlayerSelector target = context.get("target");
    this.pingTargets(
      context.getSender(),
      target.get().stream()
        .map(this.tabTPSFabric.userService()::user)
        .collect(Collectors.toList()),
      target.getInput(),
      context.get("page")
    );
  }
}
