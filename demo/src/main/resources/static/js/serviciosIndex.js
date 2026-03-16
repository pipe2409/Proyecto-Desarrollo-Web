document.addEventListener("DOMContentLoaded", function () {
  const tabs = document.querySelectorAll(".service-tab");
  const mainImage = document.getElementById("servicesMainImage");
  const visualWrap = document.querySelector(".services-visual-image-wrap");
  const caption = document.getElementById("servicesVisualCaption");

  if (!tabs.length || !mainImage || !visualWrap || !caption) return;

  function activateTab(tab) {
    tabs.forEach(item => item.classList.remove("active"));
    tab.classList.add("active");

    const newImage = tab.dataset.image || "";
    const newSubtitle = tab.dataset.subtitle || "";

    if (mainImage.getAttribute("src") !== newImage) {
      visualWrap.classList.add("is-changing");

      setTimeout(() => {
        mainImage.setAttribute("src", newImage);
        mainImage.setAttribute("alt", tab.dataset.title || "Servicio destacado");
        caption.textContent = newSubtitle;
      }, 180);

      setTimeout(() => {
        visualWrap.classList.remove("is-changing");
      }, 420);
    } else {
      caption.textContent = newSubtitle;
    }
  }

  tabs.forEach(tab => {
    tab.addEventListener("click", function () {
      activateTab(tab);
    });
  });

  activateTab(document.querySelector(".service-tab.active") || tabs[0]);
});
