htmx.on("htmx:beforeRequest", (event) => {
  const elt = event.detail.elt;
  if (
    event.detail.boosted === true &&
    elt.tagName === "A" &&
    !elt.hasAttribute("hx-swap")
  ) {
    elt.setAttribute("hx-swap", "transition:true");
  }
});
