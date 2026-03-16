
  document.addEventListener("DOMContentLoaded", function () {
    const carousel = document.getElementById("habitacionesCarousel");
    if (!carousel) return;

    const slides = Array.from(carousel.querySelectorAll(".habitacion-slide"));
    const prevBtn = document.querySelector(".habitaciones-arrow--left");
    const nextBtn = document.querySelector(".habitaciones-arrow--right");

    function updateActiveSlide() {
      const carouselRect = carousel.getBoundingClientRect();
      const carouselCenter = carouselRect.left + carouselRect.width / 2;

      let closestSlide = null;
      let closestDistance = Infinity;

      slides.forEach(slide => {
        const rect = slide.getBoundingClientRect();
        const slideCenter = rect.left + rect.width / 2;
        const distance = Math.abs(carouselCenter - slideCenter);

        slide.classList.remove("is-active");

        if (distance < closestDistance) {
          closestDistance = distance;
          closestSlide = slide;
        }
      });

      if (closestSlide) {
        closestSlide.classList.add("is-active");
      }
    }

    function getScrollAmount() {
      const firstSlide = slides[0];
      if (!firstSlide) return 400;
      const style = window.getComputedStyle(carousel);
      const gap = parseFloat(style.gap) || 0;
      return firstSlide.offsetWidth + gap;
    }

    if (prevBtn) {
      prevBtn.addEventListener("click", function () {
        carousel.scrollBy({ left: -getScrollAmount(), behavior: "smooth" });
      });
    }

    if (nextBtn) {
      nextBtn.addEventListener("click", function () {
        carousel.scrollBy({ left: getScrollAmount(), behavior: "smooth" });
      });
    }

    carousel.addEventListener("scroll", () => {
      window.requestAnimationFrame(updateActiveSlide);
    });

    window.addEventListener("resize", updateActiveSlide);

    updateActiveSlide();
  });

