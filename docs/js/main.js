document.addEventListener("DOMContentLoaded", () => {
  const cards = document.querySelectorAll(".card, .profile-row, .tech-item");
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.style.opacity = "1";
          entry.target.style.transform = "translateY(0)";
        }
      });
    },
    { threshold: 0.1 }
  );

  cards.forEach((el) => {
    el.style.opacity = "0";
    el.style.transform = "translateY(12px)";
    el.style.transition = "opacity 0.45s ease, transform 0.45s ease";
    observer.observe(el);
  });
});
